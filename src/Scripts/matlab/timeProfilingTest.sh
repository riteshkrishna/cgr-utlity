#!/bin/sh
start_time=`date +%s`

for i in {1..10000000}
do
	echo 'HELLO'
done

end_time=`date +%s`
echo execution time was `expr $end_time - $start_time` s
