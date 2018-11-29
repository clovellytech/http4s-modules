
create schema if not exists ct_permissions;

create table if not exists ct_permissions.permission (
  permission_id uuid not null primary key default gen_random_uuid(),
  name character varying (255) not null,
  description text not null default '',
  app_name character varying (255) not null
);

create table if not exists ct_permissions.user_permission (
  user_permission_id uuid not null primary key default gen_random_uuid(),
  permission_id uuid not null references ct_permissions.permission,
  user_id uuid not null references ct_auth.user
);

