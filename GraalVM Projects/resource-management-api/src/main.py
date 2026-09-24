"""FastAPI app — portable, no Cloudflare-specific imports.

Runs identically under local `uvicorn main:app` and inside the Cloudflare
Python Worker (see cloudflare_entry.py, the only Cloudflare-specific file).
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from routers.admin_users import router as admin_users_router
from routers.beds import router as beds_router
from routers.doctors import router as doctors_router
from routers.insights import router as insights_router
from routers.me import router as me_router
from routers.organization import router as organization_router
from routers.procedures import router as procedures_router
from routers.wards import router as wards_router

app = FastAPI(title="resource-management-api")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # TODO: lock to the deployed UI origin(s)
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(admin_users_router)
app.include_router(beds_router)
app.include_router(doctors_router)
app.include_router(insights_router)
app.include_router(me_router)
app.include_router(organization_router)
app.include_router(procedures_router)
app.include_router(wards_router)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}
