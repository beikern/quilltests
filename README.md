# quilltests #

Welcome to quilltests!

## What this project does ##

This is a small Quill App to test the new Context implementation added in Quill 1.1.0 using the CassandraContext

It also uses Akka Streams and type classes to persist different types in a stream manner.

The following schema has to be created in Cassandra 

```
CREATE KEYSPACE quill_test
WITH durable_writes = true
AND replication = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};


CREATE TABLE quill_test.foo_table (
	f1 text,
	f2 int,
	PRIMARY KEY (f1)
);

CREATE TABLE quill_test.bar_table (
	wololo text,
	oyoyoy int,
	PRIMARY KEY (wololo)
);

```

then just sbt run.

This implementation fails because high concurrency punches the Datastax java driver hard. There should be a configuration to avoid this behaviour.
