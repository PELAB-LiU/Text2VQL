#!/bin/bash

RUN wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar \
    -O ./jdbc/slf4j-api-1.7.36.jar
RUN wget https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar \
    -O ./slf4j-simple-1.7.36.jar
RUN wget https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.44.0.0/sqlite-jdbc-3.44.0.0.jar \
    -O ./sqlite-jdbc-3.44.0.0.jar
RUN wget https://repo1.maven.org/maven2/org/apache/commons/commons-csv/1.10.0/commons-csv-1.10.0.jar \
    -O ./commons-csv-1.10.0.jar
