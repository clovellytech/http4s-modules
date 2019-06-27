create schema if not exists ct_invitations;

create table if not exists ct_invitations.invitation (
  invitation_id uuid not null primary key default gen_random_uuid(),
  from_user uuid not null references ct_auth.user (user_id) on delete cascade,
  to_email text not null,
  to_name text not null,
  code text not null,
  create_time timestamp with time zone not null default now(),
  send_time timestamp with time zone,
  open_time timestamp with time zone,
  accept_time timestamp with time zone,
  reject_time timestamp with time zone,
  message text
);
