#!/bin/bash

OUT='whadl.latest.js';
echo -n "" > $OUT;

for dir in `ls -R | grep -v svn | grep ^\./mentalsmash | cut -d':' -f1`; do
	for file in `ls $dir/*.js`; do
		cat $file >> $OUT;
		echo >> $OUT;
		echo >> $OUT;
	done
done

cat $OUT;