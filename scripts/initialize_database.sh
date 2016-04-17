#!/bin/bash

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"
DB=$ROOT/scodex.db
SCHEMA=$ROOT/conf/schema.sql
EXE=sqlite3

if [ ! -d $ROOT ]; then
    echo "Not project root dir: $ROOT. Aborting."
    exit 1
fi

if [ -f $DB ]; then
    echo "Database exists: $DB. Aborting."
    exit 1
fi

if [ ! -f $SCHEMA ]; then
    echo "Unable to find schema: $SCHEMA. Aborting."
    exit 1
fi

command -v $EXE >/dev/null 2>&1 || { echo >&2 "Missing executable: $EXE. Aborting."; exit 1; }

echo -n "Initializing database $DB ... "

cat $SCHEMA | $EXE $DB

if [ $? -eq 0 ]; then
    echo "done!"
else
    echo "unexpected failure: $?"
fi
