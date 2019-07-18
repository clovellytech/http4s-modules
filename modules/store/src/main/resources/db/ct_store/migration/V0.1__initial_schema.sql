create schema if not exists ct_store;

create table if not exists ct_store.item (
  item_id uuid not null primary key default gen_random_uuid(),
  title text not null,
  description text not null,
  price money not null,
  create_by uuid not null references ct_auth.user (user_id) on delete cascade,
  create_date timestamp with time zone not null default now()
);

create table if not exists ct_store.order (
  order_id uuid not null primary key default gen_random_uuid(),
  create_date timestamp with time zone not null default now(),
  submit_date timestamp with time zone,
  create_by uuid not null references ct_auth.user (user_id) on delete cascade,
  fulfilled_date timestamp with time zone,
  total_price money not null
);

create table if not exists ct_store.order_item (
  order_id uuid not null references ct_store.order on delete cascade,
  item_id uuid not null references ct_store.item on delete cascade,
  order_price money not null,
  quantity integer not null default 1
);
