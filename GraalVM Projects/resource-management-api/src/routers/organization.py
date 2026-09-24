"""GET /organization — see docs/features/internationalization-i18n.md.

Unauthenticated on purpose: the frontend needs the organization's default
language (and, per tenant-branding-customization.md, branding) before a
user has signed in — e.g. to render the sign-in screen correctly. Only the
locale-relevant fields are exposed here; branding fields (logo/theme) are
that other feature's concern and would extend this same response additively.

This app is single-tenant per deployment (see tenant-branding-customization.md's
schema note), so there's always exactly one row — no `id` path param needed.
"""

from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from database import Database, get_database
from models import Organization
from payload import build_envelope, make_link

router = APIRouter(tags=["organization"])


@router.get("/organization")
async def get_organization(db: Database = Depends(get_database)) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM organizations LIMIT 1", ())
    if row is None:
        raise HTTPException(status_code=404, detail="organization not configured")
    organization = Organization(
        id=row["id"], name=row["name"], default_language=row["default_language"], timezone=row["timezone"]
    )
    return build_envelope(
        "organization", organization.model_dump(mode="json"), meta_links={"self": make_link("/organization")}
    )
