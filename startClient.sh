#!/bin/bash
echo Please select an algorithm type: lrr, fc, ott
read algoType

echo Compiling java classes...
javac stage2/*.java
echo Running java implementation...
cd stage2
java Clientv2 $algoType
echo Completed! You can safely close this window.
