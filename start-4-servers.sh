# Bash script to launch 4 servers on $PORT to $PORT + 3
# Run as root probably because you need to bind to inet interface

PORT=1000
END=$(($PORT + 3))

for i in $(seq $PORT $END);
do
	# echo $i;
	python3 server.py $i &
done;

