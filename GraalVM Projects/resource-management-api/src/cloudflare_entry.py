"""The only Cloudflare-specific file.

Adapts the portable FastAPI app (main.py) to run as a Cloudflare Python
Worker via the runtime's built-in ASGI server.
"""

from workers import asgi

from main import app

Default = asgi.entrypoint(app)
