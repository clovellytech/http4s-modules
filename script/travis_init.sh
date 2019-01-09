#!/bin/bash

psql -c 'create database ct_auth_test;' -U postgres 
psql -c 'create database ct_permissions_test;' -U postgres 
psql -c 'create database ct_files_test;' -U postgres 
psql -c 'create database ct_feature_requests_test;' -U postgres  

git clone https://github.com/zakpatterson/tsec
cd tsec/
git checkout 0.1.1-ct
sbt publishLocal
cd ..
