# Scala version of the project

The project is structured as 1 sbt project with 3 sub-modules:
* The server
* The client
* A shared library for data mappings

# Running it
```
bash complile-and-run.sh # starts 4 servers on ports 1000, 1001, 1002 and 1003
bash compile-and-run-client.sh # runs the client to generate a 1920x1080 sized image and times the process
```
