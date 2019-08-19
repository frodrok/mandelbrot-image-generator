#!/bin/bash

set -e

go build
go run main.go MandelbrotCalculator.go 1000
