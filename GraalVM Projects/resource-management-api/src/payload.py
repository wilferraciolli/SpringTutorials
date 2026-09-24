"""Shared response-envelope helpers — every endpoint returns this shape.

    {
      "_data": { "<resource_key>": <item> | [<item>, ...] },
      "_metadata": { "<field>": { "mandatory": bool, "values": [{"id", "value"}, ...] } },
      "_metaLinks": { "self": { "href": "<collection url>" } }
    }

`_data` uses the resource's singular key for both a single item and a list
of them (e.g. `_data.bed` is a list for GET /beds, a single object for
GET /beds/{id}) — same shape either way, only the value's cardinality
differs. Each item under `_data` carries its own `links` (self/update/
delete), one entry per operation that's *actually implemented* for that
resource — never fabricate a link to an endpoint that doesn't exist.
`_metadata` is for client-driven UI (validation rules, and — when a field
has an `id`-based value set — the (id, display value) pairs a selector
would need); it's keyed by field name, not by item, since it describes the
resource's shape once, not each instance.
"""

from typing import Any


def make_link(href: str) -> dict[str, str]:
    return {"href": href}


def build_resource_links(
    *, self_href: str, update_href: str | None = None, delete_href: str | None = None
) -> dict[str, dict[str, str]]:
    links = {"self": make_link(self_href)}
    if update_href:
        links["update"] = make_link(update_href)
    if delete_href:
        links["delete"] = make_link(delete_href)
    return links


def field_metadata(*, mandatory: bool, values: list[tuple[str, str]] | None = None) -> dict[str, Any]:
    metadata: dict[str, Any] = {"mandatory": mandatory}
    if values is not None:
        metadata["values"] = [{"id": id_, "value": value} for id_, value in values]
    return metadata


def build_envelope(
    resource_key: str,
    data: Any,
    *,
    metadata: dict[str, Any] | None = None,
    meta_links: dict[str, dict[str, str]] | None = None,
) -> dict[str, Any]:
    envelope: dict[str, Any] = {"_data": {resource_key: data}}
    if metadata is not None:
        envelope["_metadata"] = metadata
    if meta_links is not None:
        envelope["_metaLinks"] = meta_links
    return envelope
