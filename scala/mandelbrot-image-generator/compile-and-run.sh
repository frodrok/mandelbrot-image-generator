#!/bin/bash

sbt assembly
#scala server/target/scala-2.13/mandelbrot-generator-server_2.13-0.1.0-SNAPSHOT.jar 1000
scala server/target/scala-2.13/mandelbrot-generator-server-assembly-0.1.0-SNAPSHOT.jar 1000
