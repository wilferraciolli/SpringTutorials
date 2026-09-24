-- App-owned roles (replaces the Clerk public_metadata.roles claim as the
-- source of truth). A user can hold multiple roles (ADMIN, PROVIDER_ADMIN,
-- SALES_ADMIN, ...). CurrentUserService bootstraps this table from the
-- caller's JWT roles claim the first time a given app_user is seen with no
-- rows here yet; every login after that, this table is authoritative and
-- admin-editable via PUT /api/admin/users/{id}.
create table user_role (
    user_id    uuid        not null references app_user (id) on delete cascade,
    role       varchar(40) not null,
    created_at timestamptz not null default now(),
    primary key (user_id, role)
);
create index idx_user_role_user_id on user_role (user_id);
