# Scala version of the project

The project is structured as 1 sbt project with 3 sub-modules:
* The server
* The client
* A shared library for data mappings

When you're doing it in scala, why not use the Erlang actor model and go with Akka? :)

# Running it
```
sbt server/assembly
scala server/target/scala-2.13/mandelbrot-generator-server-assembly-0.1.0-SNAPSHOT.jar

sbt client/run &
```