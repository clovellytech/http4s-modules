#!/bin/bash

# For files
mkdir /tmp/filestest


# Test databases
psql -c 'create database ct_auth_test;' -U postgres 
psql -c 'create database ct_permissions_test;' -U postgres 
psql -c 'create database ct_files_test;' -U postgres 
psql -c 'create database ct_feature_requests_test;' -U postgres  
psql -c 'create database h4sm_docs_gen' -U postgres
