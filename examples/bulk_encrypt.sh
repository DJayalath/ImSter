#!/bin/bash

# Encrypts every picture in a given folder with the same message and password

# $1 = path to ImSter jar
# $2 = folder containing png files
# $3 = message
# $4 = password

for file in $2/*.png
do
    java -jar $1 encode -i $file -o "$(basename $file .png)OUT.png" -m $3 -p $4
done
