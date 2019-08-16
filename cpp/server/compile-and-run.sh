g++ main.cpp MandelbrotData.cpp MandelbrotCalculator.cpp -I. -std=c++17 -o server.bin

if [[ $? == 1 ]]; then
    echo "compile failed";
else
    ./server.bin 1000
fi
