#!/bin/sh
# Compile and run Task01
cd "$(dirname "$0")"
mkdir -p out output
javac -d out src/*.java
java -cp out FeeServiceTest | tee output/FeeServiceTest_output.txt
java -cp out DispatchBench | tee output/DispatchBench_output.txt
# To open the window (needs a desktop): java -cp out FeeWindow
