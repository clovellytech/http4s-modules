create schema if not exists ct_messages;
create extension if not exists pgcrypto with schema public;

create table if not exists ct_messages.message(
	message_id uuid primary key default gen_random_uuid(),
  from_user_id uuid not null references ct_auth.user (user_id) on delete cascade,
  to_user_id uuid not null references ct_auth.user (user_id) on delete cascade,
	content text not null,
  create_date timestamp with time zone not null default now(),
  open_date timestamp with time zone
);
