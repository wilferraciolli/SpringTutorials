-- Phase 2 admin: distinguish admin-created (MANAGED) users from those who
-- signed up themselves (SELF_SERVICE). See docs/05-security-and-accounts.md.

alter table app_user
    add column ownership varchar(16) not null default 'SELF_SERVICE';

-- MANAGED users have no login, so no OIDC subject.
alter table app_user
    alter column auth_subject drop not null;
