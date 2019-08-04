#!/bin/bash

set -e

mvn clean
mvn install
mvn package
java -jar target/mandelbrot-generator-1.0-SNAPSHOT.jar 1000 &
java -jar target/mandelbrot-generator-1.0-SNAPSHOT.jar 1001 &
java -jar target/mandelbrot-generator-1.0-SNAPSHOT.jar 1002 &
java -jar target/mandelbrot-generator-1.0-SNAPSHOT.jar 1003 &
