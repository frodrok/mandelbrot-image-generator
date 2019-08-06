#!/bin/bash

set -e

mvn install package -pl server

#pkill java

java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1000 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1001 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1002 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1003 &
