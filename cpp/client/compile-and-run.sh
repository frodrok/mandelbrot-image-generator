make clean && make

if [[ $? > 0 ]]; then
    echo "compile failed";
else
    ./client.bin 2 640 480 255 output.png
fi
