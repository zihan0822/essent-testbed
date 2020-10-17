#!/bin/bash
TESTDIRECTORY="riscv-mini/src/test/resources"
ESSENTDIRECTORY="essent-testbed/riscv-mini"
RISCDIRECTORY="riscv-mini"

cd $TESTDIRECTORY
i=0
for file in *; do 
    if [ -f "$file" ]; then 
        tests[$i]=$file
        (( i++ ))
    fi 
done
cd "../../.."
mkdir "tests"
cd ".."
mkdir "tests"
mkdir "testDifferences"

for test in "${tests[@]}"
do
    echo "$test:"
    cd $RISCDIRECTORY
    VERILATOR= ./VTile "./src/test/resources/$test" 2>&1 | tee "tests/$test.out" > /dev/null
    if [ -z "$VERILATOR" ]; then
        echo "Verilator Worked"
    fi
    cd ..
    ESSENT= ./top "$TESTDIRECTORY/$test" > "tests/$test.out"
    if [ -z "$ESSENT" ]; then
        echo "ESSENT Worked"
    fi
    diff -w "$RISCDIRECTORY/tests/$test.out" "./tests/$test.out" > "testDifferences/$test.out"
    echo 
done
