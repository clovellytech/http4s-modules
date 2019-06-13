---
id:     index
title:  Http4s Modules by Example
---

This project as a whole was inspired by the petstore project created by Paul Cleary, [here](https://github.com/pauljamescleary/scala-pet-store). In this section we will demonstrate how to create a new http4s module from scratch, emulating a petstore as in that project. Our steps will be as follows:

1. [Build an initial schema in SQL](01-initialSchema.md)
2. [Write and test updates and queries that will interact with our database](02-queries.md)
3. [Build and test endpoints that our users can connect to](03-endpoints.md)
4. [Create a server that will wire everything together and serve our endpoints to the user](04-server.md)

In this project, our users are either petstore employees or customers. Customers may sign up, log in, view and order pets. In addition to that, employees may delete orders, create and update pet profiles.

Our module directory will have the following hierarchy:


```
./petstore/src
├── main
│   ├── resources
│   │   ├── reference.conf
│   │   ├── db
│   │   │   └── ct_petstore
│   │   │       └── migration
│   │   │           ├── V0.1__initial_schema.sql
│   └── scala
│       └── h4sm
│           └── petstore
│               ├── client          # Client requests, initially beneficial for testing
│               ├── db
│               │   └── sql         # Doobie SQL Query/Update definitions
│               ├── domain          # Entities and Algebras for interacting with data
│               ├── infrastructure
│               │   ├── endpoint    # Http4s endpoints, codecs, and json transactional entities
│               │   └── repository  
│               │       └── persistent  # Database backed interpreters for algebras
                                        # marked "persistent" here, because other interpreters could exist, such as in-memory
```

Note the structure of the resources directory. Petstore will depend on other modules such as auth, permissions, and files. This means that those schema definitions will be resolvable under `resources/db/{ct_auth,ct_permissions,ct_files}`, along side of `ct_petstore`. Tables required by those dependencies will be automatically migrated, as shown in the next document.

Continue on to the first page, [writing our initial schema](01-initialSchema.md)
