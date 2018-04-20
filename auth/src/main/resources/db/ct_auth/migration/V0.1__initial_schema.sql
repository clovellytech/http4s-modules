-- Initial table namespace ands schema for auth project

-- convenience function to check existence of constraint before adding.
-- https://stackoverflow.com/questions/6801919/postgres-add-constraint-if-it-doesnt-already-exist

create or replace function create_constraint_if_not_exists (
    t_name text, c_name text, constraint_sql text
)
returns void AS
$$
begin
    -- Look for our constraint
    if not exists (select constraint_name
                   from information_schema.constraint_column_usage
                   where table_name = t_name  and constraint_name = c_name) then
        execute constraint_sql;
    end if;
end;
$$ language 'plpgsql';


create schema if not exists ct_auth;
create extension if not exists pgcrypto;

create table if not exists ct_auth.user(
	user_id uuid primary key default gen_random_uuid(),
	username text not null,
	join_date timestamp with time zone not null default now(),
	hash bytea not null
);

select create_constraint_if_not_exists(
    'ct_auth.user',
    'user_username_unique',
    'alter table ct_auth.user add constraint user_username_unique unique (username)'
);
