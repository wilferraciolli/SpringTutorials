FROM python:3.13-slim

RUN pip install --no-cache-dir uv

WORKDIR /app

COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-dev

COPY src/ src/
COPY migrations/ migrations/
COPY scripts/ scripts/

ENV PATH="/app/.venv/bin:$PATH"

EXPOSE 8001

CMD ["uv", "run", "uvicorn", "main:app", "--app-dir", "src", "--host", "0.0.0.0", "--port", "8001"]
