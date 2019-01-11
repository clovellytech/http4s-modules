#!/bin/bash

# For files
mkdir /tmp/filestest


# Test databases
psql -c 'create database ct_auth_test;' -U postgres 
psql -c 'create database ct_permissions_test;' -U postgres 
psql -c 'create database ct_files_test;' -U postgres 
psql -c 'create database ct_feature_requests_test;' -U postgres  
psql -c 'create database h4sm_docs_gen' -U postgres


# Modules we have to publish locally because we're 
# waiting on releases from them.
git clone https://github.com/zakpatterson/http4s
cd http4s
git checkout v0.20.0-M5-ct
sbt publishLocal
cd ..

git clone https://github.com/zakpatterson/tsec
cd tsec/
git checkout 0.1.1-ct
sbt publishLocal
cd ..
