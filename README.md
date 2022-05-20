# Readme

Extracts raw text from web archives (WARCs), usually obtained by web crawling.

## Requirements

* Java 8
* Maven 3 (for development)

## Note

Test coverage is currently not representative because some test that 
relied on data that we do not want make public have been removed for 
the public release.

This will be fixed for the next release.

## Building from source

To build jwarcex from source you need to invoke `mvn install` within the main directory.

```bash
cd jwarcex
mvn clean install
```

For command line usage you will find a jar file under `jwarcex/jwarcex-standalone/target/jwarcex-standalone-<VERSION>.jar`

## Examples

**Print the help**
```
java -jar jwarcex-standalone-<VERSION>-SNAPSHOT.jar -h
```

**Process a WARC file**
```
java -jar jwarcex-standalone-<VERSION>-SNAPSHOT.jar /path/to/my.warc /path/to/output.source
```

**Read gzipped WARCs**
```
java -jar jwarcex-standalone-<VERSION>-SNAPSHOT.jar /path/to/my.warc.gz /path/to/output.source -c
```
