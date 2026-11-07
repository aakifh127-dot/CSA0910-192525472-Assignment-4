#!/bin/sh
# Compile and run Task02
cd "$(dirname "$0")"
mkdir -p out output
javac -d out src/*.java
java -cp out AppointmentTests | tee output/AppointmentTests_output.txt
java -cp out CatalogBench | tee output/CatalogBench_output.txt
# To open the window (needs a desktop; the trace lines appear on the console): java -cp out AppointmentDesk
