"""Bed-occupancy policy: how long a bed may stay 'occupied' before it's flagged."""

from datetime import UTC, datetime
from typing import NamedTuple

DEFAULT_MAX_OCCUPIED_MINUTES = 120
MIN_EXPECTED_DURATION_MINUTES = 15
MAX_EXPECTED_DURATION_MINUTES = 1440
VARIANCE_THRESHOLD_PERCENT = 10


class OccupancyMetrics(NamedTuple):
    occupied_minutes: int | None
    is_overdue: bool
    overdue_minutes: int
    remaining_minutes: int | None


class VarianceResult(NamedTuple):
    variance_minutes: int
    variance_percent: float
    variance_class: str  # 'underutilized' | 'on_track' | 'overrun'


def compute_overdue(
    status: str,
    status_changed_at: str,
    expected_duration_minutes: int | None,
) -> OccupancyMetrics:
    if status != "occupied":
        return OccupancyMetrics(None, False, 0, None)
    changed_at = datetime.fromisoformat(status_changed_at)
    occupied_minutes = int((datetime.now(UTC) - changed_at).total_seconds() // 60)
    threshold = expected_duration_minutes or DEFAULT_MAX_OCCUPIED_MINUTES
    is_overdue = occupied_minutes > threshold
    overdue_minutes = max(occupied_minutes - threshold, 0)
    remaining_minutes = threshold - occupied_minutes
    return OccupancyMetrics(occupied_minutes, is_overdue, overdue_minutes, remaining_minutes)


def clamp_duration(minutes: int) -> int:
    return max(MIN_EXPECTED_DURATION_MINUTES, min(minutes, MAX_EXPECTED_DURATION_MINUTES))


def compute_variance(actual_minutes: int, expected_minutes: int) -> VarianceResult:
    """Rule 4 (docs/features/bed-timeframe-management.md) — run once, at
    discharge, and the result stored (see bed_occupancy_history) rather
    than recomputed from live data, so historical analytics stay stable
    even if `expected_duration_minutes` policy defaults change later.
    """
    variance_minutes = actual_minutes - expected_minutes
    variance_percent = (variance_minutes / expected_minutes) * 100 if expected_minutes else 0.0
    if variance_percent < -VARIANCE_THRESHOLD_PERCENT:
        variance_class = "underutilized"
    elif variance_percent > VARIANCE_THRESHOLD_PERCENT:
        variance_class = "overrun"
    else:
        variance_class = "on_track"
    return VarianceResult(variance_minutes, variance_percent, variance_class)
