#!/bin/sh
# Compile and run Task04
cd "$(dirname "$0")"
mkdir -p out output
javac -d out src/*.java 2>&1 | grep -v "^Note:\|warning\|sun.misc\|\^\|^1 warning\|Unsafe" || true
java -Djava.awt.headless=true -cp out PaintCheck | tee output/PaintCheck_output.txt
java -Djava.awt.headless=true -cp out PaintBench | tee output/PaintBench_output.txt
# Task 4.4: record the compiler message when @Override is added to the misspelled method
mkdir -p out_err
javac -d out_err extras/DrawPadOverrideError.java > output/OverrideError_output.txt 2>&1 || true
cat output/OverrideError_output.txt
# To open the real window (needs a desktop): java -cp out DrawPad
