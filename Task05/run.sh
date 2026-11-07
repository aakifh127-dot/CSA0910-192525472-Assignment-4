#!/bin/sh
# Compile and run Task05
cd "$(dirname "$0")"
mkdir -p out output
javac -d out src/*.java 2>&1 | grep -v "^Note:\|warning\|sun.misc\|\^\|Unsafe" || true
java -Djava.awt.headless=true -cp out RatesTest | tee output/RatesTest_output.txt
java -Djava.awt.headless=true -cp out ConverterCheck | tee output/ConverterCheck_output.txt
java -Djava.awt.headless=true -cp out TriggerCount | tee output/TriggerCount_output.txt
sh ChangeEffort.sh | tee output/ChangeEffort_output.txt
# To open a window (needs a desktop): java -cp out Converter   (also ConverterA..D, ConverterA2..D2, ConverterShared)
