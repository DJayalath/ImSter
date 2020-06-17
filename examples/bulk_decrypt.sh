#!/bin/bash

# Decrypts every picture in a given folder assuming they all have the same password

# $1 = path to ImSter jar
# $2 = folder containing png files
# $3 = password

for file in $2/*.png
do
    java -jar $1 decode -i $file -p $3
done
