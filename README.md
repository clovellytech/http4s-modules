FeatureRequests
============

This aims to be a simple and easy microservice for tracking requested features from users.

It is based very closely on the [Scala-pet-store example project](https://github.com/pauljamescleary/scala-pet-store)

Setup
---
* We're assuming postgres, and the default connection settings are the same as postgres's defaults. Check `reference.conf`. These settings can be overridden by a file named `application.conf` on your classpath.  
* Two databases are expected to exist: `feature_requests` and `feature_requests_test`.  All database migrations are applied into these databases.

Migrations
--
* To start a new database migration, run `./new_migration.sh [migration_name_here]`. This will timestamp a new migration into the correct directory. The migration name should not contain whitespace or `.sql`. That script will create the migration file and put you into an editor to complete it.


