create schema if not exists ct_feature_requests;

create table if not exists ct_feature_requests.feature_requests (
  feature_request_id bigserial primary key,
  requesting_user_id uuid,
  create_date timestamp with time zone not null default now(),
  title text not null,
  description text not null
);

create table if not exists ct_feature_requests.vote (
  vote_id bigserial primary key,
  feature_request_id bigint references ct_feature_requests.feature_request,
  create_date timestamp with time zone not null default now(),
  by_user_id uuid,
  vote smallint,
  comment text,
  unique(feature_request_id, by_user_id)
);
