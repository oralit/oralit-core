# ORALIT

Oralit is a database monitoring tools.

### Install Oracle JDBC

[See this post](https://blogs.oracle.com/dev2dev/get-oracle-jdbc-drivers-and-ucp-from-oracle-maven-repository-without-ides) to use Oracle JDBC properly. Or, you could download the JAR file, and then execute this command:

`mvn install:install-file -DgroupId=com.oracle -DartifactId=oracle-jdbc8 -Dversion=12c -Dpackaging=jar -Dfile=<THE_JDBC_JAR_LOCATION>`

