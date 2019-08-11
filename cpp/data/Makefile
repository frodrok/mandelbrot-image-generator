
INC = -I ./MandelbrotData.h

executable: main.o MandelbrotData.o
	g++ main.o MandelbrotData.o
	
main.o: main.cpp MandelbrotData.o
	g++ $(INC) -c main.cpp

MandelbrotData.o: MandelbrotData.cpp MandelbrotData.h
	g++ $(INC) -c MandelbrotData.cpp
