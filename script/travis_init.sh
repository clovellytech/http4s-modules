#!/bin/bash

# For files
mkdir /tmp/filestest

# docs gen database
psql -c 'create database h4sm_docs_gen' -U postgres
