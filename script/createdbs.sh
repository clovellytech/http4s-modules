#!/bin/bash

psql -U postgres -h localhost -p 5432 -c "create database ct_auth_test"
psql -U postgres -h localhost -p 5432 -c "create database ct_feature_requests_test"

