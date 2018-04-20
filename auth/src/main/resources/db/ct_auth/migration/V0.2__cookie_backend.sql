create table if not exists ct_auth.authenticated_cookie (
    cookie_id text primary key not null,
    name text not null,
    content text not null,
    user_identity uuid references ct_auth.user (user_id),
    expiry timestamp with time zone not null,
    last_touched timestamp with time zone,
    secure boolean not null,
    http_only boolean not null default true,
    domain text,
    path text,
    extension text
);
