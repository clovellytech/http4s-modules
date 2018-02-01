#!/bin/bash 

dir="./db/src/main/resources/migration/"
time=`date '+%Y%m%d%H%M%S'`
file=$dir/V${time}__$1.sql
touch $file && emacs $file -nw

