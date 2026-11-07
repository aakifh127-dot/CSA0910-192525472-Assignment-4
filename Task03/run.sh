#!/bin/sh
# Compile and run Task03 (headless: panels are laid out and painted to PNG files)
cd "$(dirname "$0")"
mkdir -p out output
javac -d out src/*.java
java -Djava.awt.headless=true -cp out ResizeCheck | tee output/ResizeCheck_output.txt
java -Djava.awt.headless=true -cp out LayoutBench | tee output/LayoutBench_output.txt
# To open the real window (needs a desktop): java -cp out LibraryFrame
