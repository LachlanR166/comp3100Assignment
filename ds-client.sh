#!/bin/bash

cd ds-sim/src/pre-compiled

echo Enter algorithm type lrr, fc, ff, wf, bf
read algoType

./ds-client -n -a $algoType
