# ExtraRelaTable

ExtraRelaTable is a thesis project developed by Václav Brodec at
Faculty of Mathematics and Physics of Charles University in Prague,
which applies bottom-up approach to assignment of RDF properties to
numeric table columns; these properties identify the relations formed
between the columns and the subject of the table. The project follows
and complements preceding work of Neumaier et al., which uses more
general top-down approach to labeling of numeric columns.

The algorithm is exposed through public REST API at [http://localhost:8080/extrarelatable/](http://localhost:8080/extrarelatable/),
supposed that the packaged WAR is deployed in a local servlet
container on default port. ERT may optionally use available running
instance of Odalic STI tool ([https://github.com/odalic](https://github.com/odalic))
to provide additional context, which increases the effectiveness of
the algorithm.

Alongside the main application, a small experimental framework
is included, implemented as a set of JUnit tests, which allow to
thoroughly evaluate the algorithm on various supported data sets.

The project is documented in the accompanying thesis Discovering and
Creating Relations among CSV Columns using Linked Data Knowledge
Bases.

## Installation guide and documentation

### Building from source files
- Checkout the sources and accompanying resources from the Git repository.
- Run *mvn package* in the project directory, the produced .war file will be located in /target sub-directory. Copy the .war file to the deployment directory of the application server/servlet container.

#### Configuration 
The minimum required configuration options (provided as system properties to the Java Virtual Machine hosting the application server) are the following:
  - eu.odalic.extrarelatable.graphsPath - absolute path to a directory with initial compatible referential data sets.
  - eu.odalic.extrarelatable.db.filePath - absolute path to a database file. It will be created if not yet created or missing.

### Referential data sets
The referential data sets are provided as non-integral part of the thesis. Custom or newly created data sets have to follow the format established and described in the thesis.

## Acknowledgement

Many thanks to Sebastian Neumaier and others for their work on labeling of numeric columns, which served as inspiration and point of reference.

To both doc. Mgr. Martin Nečaský, Ph.D. and RNDr. Tomáš Knap, Ph.D. for their attentive supervision.

## License
Apache 2.0
