
Imaginary Data Generator
========================

This project contains a program that generates imaginary ("fake") data from the
equally imaginary Ga astrometrics observatory. The purpose of the IDG is create
large quantities of data on demand for use in my database classes such as Big
Data Processing.

To compile and execute, use [SBT](https://www.scala-sbt.org/index.html). For
example:

```bash
# Print usage information.
sbt 'run --help'

# Run with the default options.
sbt run

# Generate 20 stars.
sbt 'run --count 20'

# Write observations to HDFS.
sbt 'run --observations-path hdfs://10.0.0.254:9000/user/jaudet/observations.csv'
```

One can create a redistributable JAR file with ``sbt package``, but this file
doesn't contain dependencies.
