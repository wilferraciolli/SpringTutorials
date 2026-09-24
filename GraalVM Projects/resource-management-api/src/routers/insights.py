"""Insights & analytics — see docs/features/insights-analytics.md.

Only the metrics honestly derivable from data this app actually records
are implemented: a live snapshot (`/overview`), and two aggregates over
`bed_occupancy_history` (populated at discharge — see routers/beds.py's
`_record_discharge`, added by this feature since bed-timeframe-management.md
deferred it). Endpoints requiring point-in-time historical snapshots
(`availability-timeline`, a heatmap by day) or reconstructed state-transition
timing (`bed-state-transitions`) aren't implemented — the event log this app
keeps (status changes + duration history) doesn't retroactively contain
either without a separate snapshot/polling mechanism, and time-series
trend bucketing (`occupancy-duration-trend`) was cut for scope alongside
them.
"""

from typing import Any

from fastapi import APIRouter, Depends

from auth import get_current_user_id
from database import Database, get_database
from models import InsightsOverview, VarianceBucket, VarianceDistribution, WardDurationAnalysis
from payload import build_envelope, make_link
from policy import compute_overdue

router = APIRouter(prefix="/insights", tags=["insights"])


def _history_filter(ward_id: str | None, date_from: str | None, date_to: str | None) -> tuple[str, list[Any]]:
    conditions: list[str] = []
    params: list[Any] = []
    if ward_id:
        conditions.append("ward_id = ?")
        params.append(ward_id)
    if date_from:
        conditions.append("ended_at >= ?")
        params.append(date_from)
    if date_to:
        conditions.append("ended_at <= ?")
        params.append(date_to)
    where_clause = f" WHERE {' AND '.join(conditions)}" if conditions else ""
    return where_clause, params


@router.get("/overview")
async def get_overview(
    ward_id: str | None = None,
    date_from: str | None = None,
    date_to: str | None = None,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    bed_conditions, bed_params = (["ward_id = ?"], [ward_id]) if ward_id else ([], [])
    bed_where = f" WHERE {' AND '.join(bed_conditions)}" if bed_conditions else ""
    bed_rows = await db.fetch_all(f"SELECT * FROM beds{bed_where}", tuple(bed_params))

    total_beds = len(bed_rows)
    occupied_beds = sum(1 for b in bed_rows if b["status"] == "occupied")
    preparing_beds = sum(1 for b in bed_rows if b["status"] == "preparing")
    ready_beds = sum(1 for b in bed_rows if b["status"] == "ready")
    cleaning_beds = sum(1 for b in bed_rows if b["status"] == "cleaning")
    overdue_beds = sum(
        1
        for b in bed_rows
        if b["status"] == "occupied"
        and compute_overdue(b["status"], b["status_changed_at"], b["expected_duration_minutes"]).is_overdue
    )

    where_clause, params = _history_filter(ward_id, date_from, date_to)
    history_row = await db.fetch_one(
        f"SELECT AVG(actual_duration_minutes) AS avg_duration, AVG(variance_percent) AS avg_variance "
        f"FROM bed_occupancy_history{where_clause}",
        tuple(params),
    )

    overview = InsightsOverview(
        total_beds=total_beds,
        occupied_beds=occupied_beds,
        preparing_beds=preparing_beds,
        ready_beds=ready_beds,
        cleaning_beds=cleaning_beds,
        availability_rate=round((ready_beds / total_beds) * 100, 1) if total_beds else 0.0,
        occupancy_rate=round((occupied_beds / total_beds) * 100, 1) if total_beds else 0.0,
        avg_occupancy_duration=(
            round(history_row["avg_duration"], 1) if history_row and history_row["avg_duration"] is not None else None
        ),
        avg_duration_variance=(
            round(history_row["avg_variance"], 1) if history_row and history_row["avg_variance"] is not None else None
        ),
        overdue_beds=overdue_beds,
    )
    return build_envelope(
        "overview", overview.model_dump(), meta_links={"self": make_link("/insights/overview")}
    )


@router.get("/occupancy-duration-analysis")
async def get_occupancy_duration_analysis(
    ward_id: str | None = None,
    date_from: str | None = None,
    date_to: str | None = None,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    where_clause, params = _history_filter(ward_id, date_from, date_to)
    rows = await db.fetch_all(
        f"SELECT h.ward_id, w.name AS ward_name, h.variance_class, h.variance_percent, h.variance_minutes "
        f"FROM bed_occupancy_history h LEFT JOIN wards w ON w.id = h.ward_id{where_clause}",
        tuple(params),
    )

    by_ward: dict[str, dict[str, Any]] = {}
    for row in rows:
        entry = by_ward.setdefault(
            row["ward_id"],
            {
                "ward_id": row["ward_id"],
                "ward_name": row["ward_name"] or row["ward_id"],
                "total_occupancies": 0,
                "on_track": 0,
                "overrun": 0,
                "underutilized": 0,
                "_variance_sum": 0.0,
                "total_overrun_minutes": 0,
                "total_underutil_minutes": 0,
            },
        )
        entry["total_occupancies"] += 1
        entry[row["variance_class"]] += 1
        entry["_variance_sum"] += row["variance_percent"]
        if row["variance_class"] == "overrun":
            entry["total_overrun_minutes"] += row["variance_minutes"]
        elif row["variance_class"] == "underutilized":
            entry["total_underutil_minutes"] += abs(row["variance_minutes"])

    analysis = [
        WardDurationAnalysis(
            ward_id=entry["ward_id"],
            ward_name=entry["ward_name"],
            total_occupancies=entry["total_occupancies"],
            on_track=entry["on_track"],
            overrun=entry["overrun"],
            underutilized=entry["underutilized"],
            avg_variance_percent=round(entry["_variance_sum"] / entry["total_occupancies"], 1),
            total_overrun_minutes=entry["total_overrun_minutes"],
            total_underutil_minutes=entry["total_underutil_minutes"],
        ).model_dump()
        for entry in sorted(by_ward.values(), key=lambda e: e["ward_name"])
    ]
    return build_envelope(
        "duration_analysis",
        analysis,
        meta_links={"self": make_link("/insights/occupancy-duration-analysis")},
    )


_BUCKET_ORDER = ("very_underutilized", "underutilized", "on_track", "overrun", "very_overrun")


def _bucket_for(variance_percent: float) -> str:
    if variance_percent < -30:
        return "very_underutilized"
    if variance_percent < -10:
        return "underutilized"
    if variance_percent <= 10:
        return "on_track"
    if variance_percent <= 30:
        return "overrun"
    return "very_overrun"


@router.get("/duration-variance-distribution")
async def get_duration_variance_distribution(
    ward_id: str | None = None,
    date_from: str | None = None,
    date_to: str | None = None,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    where_clause, params = _history_filter(ward_id, date_from, date_to)
    rows = await db.fetch_all(
        f"SELECT variance_percent FROM bed_occupancy_history{where_clause}", tuple(params)
    )

    counts = {bucket: 0 for bucket in _BUCKET_ORDER}
    for row in rows:
        counts[_bucket_for(row["variance_percent"])] += 1
    total = len(rows)

    distribution = VarianceDistribution(
        **{
            bucket: VarianceBucket(
                count=counts[bucket], percent=round((counts[bucket] / total) * 100, 1) if total else 0.0
            )
            for bucket in _BUCKET_ORDER
        }
    )
    return build_envelope(
        "variance_distribution",
        distribution.model_dump(),
        meta_links={"self": make_link("/insights/duration-variance-distribution")},
    )
