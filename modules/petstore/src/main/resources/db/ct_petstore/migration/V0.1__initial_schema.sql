create schema if not exists ct_petstore;

create table if not exists ct_petstore.pet (
  pet_id uuid not null primary key default gen_random_uuid(),
  create_time timestamp with time zone not null default now(),
  update_time timestamp with time zone not null default now(),
  name text not null,
  bio text,
  created_by uuid not null references ct_auth.user (user_id),
  status text not null,
  photo_urls text[] not null
);

create table if not exists ct_petstore.order (
  order_id uuid not null primary key default gen_random_uuid(),
  pet_id uuid not null references ct_petstore.pet on delete cascade,
  user_id uuid not null references ct_auth.user on delete cascade,
  create_time timestamp with time zone not null default now(),
  ship_time timestamp with time zone
);
