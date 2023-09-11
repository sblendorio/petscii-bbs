#!/bin/sh
for f in *.seq; do
	echo $f
	cat $f | bbe -e 's/\x8d/\x0d/' | sponge $f
done
