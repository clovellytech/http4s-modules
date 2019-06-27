create schema if not exists ct_files;
create extension if not exists pgcrypto with schema public;

create table if not exists ct_files.file_meta (
   file_meta_id uuid not null primary key default gen_random_uuid(),
   name character varying (255),
   description text,
   user_filename character varying (255),
   creation_time timestamp with time zone not null default now(),
   uri text,
   is_public boolean not null,
   backend character varying (255),
   uploaded_by uuid not null references ct_auth.user (user_id),
   unique(uri)
);

