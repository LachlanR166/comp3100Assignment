#!/bin/bash

cd ds-sim/src/pre-compiled

echo Insert config file number 1-5
read configNo

./ds-server -n -c ../../configs/sample-configs/ds-sample-config0$configNo.xml -v brief
