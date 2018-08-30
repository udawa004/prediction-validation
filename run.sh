#!/bin/bash
#
# Use this shell script to compile (if necessary) your code and then execute it. Below is an example of what might be found in this file if your program was written in Python
#
#python ./src/prediction-validation.py ./input/window.txt ./input/actual.txt ./input/predicted.txt ./output/comparison.txt

cd ./src
javac DriverClass.java
java DriverClass /home/gami/Documents/prediction-validation-master/insight_testsuite/tests/test_1/input/actual.txt /home/gami/Documents/prediction-validation-master/insight_testsuite/tests/test_1/input/predicted.txt /home/gami/Documents/prediction-validation-master/insight_testsuite/tests/test_1/input/window.txt /home/gami/Documents/prediction-validation-master/insight_testsuite/tests/test_1/output/comparison.txt
