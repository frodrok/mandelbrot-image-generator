# Java version of the project

The project is structured with 3 maven modules:
* The server
* The client
* A shared library for json mappings

Each module compiles a .jar-file that is runnable. To start off there will be bash scripts to launch 4 servers and start the client.

### Running it
```bash
mvn install package

java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1000 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1001 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1002 &
java -jar server/target/mandelbrot-generator-server-1.0-SNAPSHOT.jar 1003 &
    
java -jar client/target/client-1.0-SNAPSHOT.jar 2 1920 1080 255 output.png
```

args[0] = how many parts you want to split into
args[1] = the width of the image
args[2] = the height of the image
args[3] = maximum iterations
args[4] = the desired filename