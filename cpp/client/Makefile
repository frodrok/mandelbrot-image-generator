CC=gcc
CFLAGS=-Winit-self -Wall -Wextra -std=c++17

all: main.o
	g++ main.o mandelbrotdata.o -o client.bin
main.o: mandelbrotdata.o
	$(CC) $(CFLAGS) -c main.cpp -o main.o
mandelbrotdata.o:
	$(CC) $(CFLAGS) -c MandelbrotData.cpp -I. -o mandelbrotdata.o
clean:
	rm *.o

