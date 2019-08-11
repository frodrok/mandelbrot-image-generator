all: main.o
	g++ main.o -o bin
main.o: main.cpp
	g++ -Wall \
	-Wextra \
	-Winit-self \
	-ansi \
	-std=c++17 \
	-I . \
	-c main.cpp
clean:
	rm -rf *.o
	rm bin
