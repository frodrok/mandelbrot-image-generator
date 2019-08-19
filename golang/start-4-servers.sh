PORT=1000
END=$(($PORT + 3))

for i in $(seq $PORT $END);
do
	# echo $i;
	./bin/server $i &
done;

