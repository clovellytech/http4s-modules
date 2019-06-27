#!/bin/bash

if [[ $# < 3 ]]; then
	echo "Usage: new_migration <project_name> <db_schema_name> <migration_name>"
	exit;
fi

dir="./$1/src/main/resources/db/$2/migration/"
time=`date '+%Y%m%d%H%M%S'`
file=$dir/V${time}__$3.sql
mkdir -p $dir
touch $file && emacs $file -nw
