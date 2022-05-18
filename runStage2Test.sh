#!/bin/bash
echo Enter an algorithm type: ott, lrr, fc
read algoType

cd stage2

./stage2-test-x86 "java Clientv2 $algoType" -o tt -n
