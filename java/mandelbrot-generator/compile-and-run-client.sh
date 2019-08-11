#!/bin/bash

set -e

mvn clean install package -pl client

java -jar client/target/client-1.0-SNAPSHOT.jar 2 1920 1080 255 output.png
