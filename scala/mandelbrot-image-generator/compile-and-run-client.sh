#!/bin/bash

set -e

sbt client/assembly

time scala client/target/scala-2.13/mandelbrot-generator-client-assembly-0.1.0-SNAPSHOT.jar 2 1920 1080 255 output.png
