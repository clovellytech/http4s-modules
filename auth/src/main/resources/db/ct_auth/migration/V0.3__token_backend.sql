create table if not exists ct_auth.token (
	token_id uuid not null primary key default gen_random_uuid(),
	secure_id bytea not null,
	user_id uuid not null references ct_auth.user on delete cascade,
	expiry timestamp with time zone not null,
	last_touched timestamp with time zone
);
