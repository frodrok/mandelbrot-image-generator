### Golang version of the project

### Running it
```bash
cd src/server && go build && go install && cd ../..
cd src/client && go build && go install && cd ../..

bash start-4-servers.sh

./bin/client 2 1920 1080 255 output.png
```

Fastest of them all so far as well as easiest to write. In terms of parallelism and socket programming.
