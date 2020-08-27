# PoC Database Poller R2DBC

This is a small PoC project for building a database poller which locks the records and ignores locked records.
By using this construction it becomes possible to run multiple database pollers, without duplicates. Key to this
that the database must support it. The following databases at least support it:

- MS SQL Server
- Oracle DB
- Postgres

This project uses the reactive database driver combined with reactive Spring Data, instead of the original project
[PoC Database Poller](https://github.com/ninckblokje/poc-db-poller), which uses JDBC, Spring Data JPA and Hibernate.

## Locking records

Records is done using a query in the repository [PollRepository](src/main/java/ninckblokje/poc/db/poller/repository/PollRepository.java):

````sql
select TOP(10) pollrecord0_.id as id, pollrecord0_.value as value from poll_record pollrecord0_ with (updlock, rowlock, readpast) where pollrecord0_.value is not null
````

## Transactions

The transaction is started by the service [PollRecordPoller](src/main/java/ninckblokje/poc/db/poller/poller/PollRecordPoller.java)
and should apply to the entire Flux stream.
 
## JUnit tests

The behaviour is tested in the class [PocDbPollerR2dbcApplicationTests](src/test/java/ninckblokje/poc/db/poller/PocDbPollerR2dbcApplicationTests.java).
By default Maven and the test will fail. Provide one of the following profiles to test it with the corresponding database:

- postgres
- sqlserver

For a failure, use the profile:

- mariadb
