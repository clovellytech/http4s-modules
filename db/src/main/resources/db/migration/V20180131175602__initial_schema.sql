create table if not exists feature_request (
  feature_request_id bigserial primary key,
  requesting_user_id uuid,
  create_date timestamp with time zone not null default now(),
  title text not null,
  description text not null
);

create table if not exists vote (
  vote_id bigserial primary key,
  feature_request_id bigint references feature_request,
  create_date timestamp with time zone not null default now(),
  by_user_id uuid,
  vote smallint,
  comment text
);
