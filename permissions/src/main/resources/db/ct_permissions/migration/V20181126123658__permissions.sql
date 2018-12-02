
create schema if not exists ct_permissions;

create table if not exists ct_permissions.permission (
  permission_id uuid not null primary key default gen_random_uuid(),
  name character varying (255) not null,
  description text not null default '',
  app_name character varying (255) not null
);

create table if not exists ct_permissions.user_permission (
  user_permission_id uuid not null primary key default gen_random_uuid(),
  permission_id uuid not null references ct_permissions.permission on delete cascade,
  user_id uuid not null references ct_auth.user on delete cascade,
  grant_time timestamp with time zone not null default now(),
  granted_by_id uuid not null references ct_auth.user (user_id)
);


insert into ct_permissions.permission (name, description, app_name)
values ('view', 'Able to view permission information', 'ct_permissions');
insert into ct_permissions.permission (name, description, app_name)
values ('admin', 'Able to change permissions', 'ct_permissions');
insert into ct_permissions.permission (name, description, app_name)
values ('view', 'Able to view user information', 'ct_auth');
