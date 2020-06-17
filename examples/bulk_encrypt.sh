#!/bin/bash

# Encrypts every picture in a given folder with a message and password

# $1 = path to ImSter jar
# $2 = folder containing png files
# $3 = password

for file in $2*.png
do
    echo -n "Enter message for $(basename $file): "
    read answer
    java -jar $1 encode -i $file -o "$file" -m "$answer"  -p $3
done
