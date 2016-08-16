# quilltests #

Welcome to quilltests!

## What this project does ##

This is a small Quill App to test the new Context implementation added in Quill 0.8.0 using the CassandraContext

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

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
