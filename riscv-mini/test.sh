#!/bin/bash

TEST_PROGRAM_DIR="riscv-mini/src/test/resources"

VER_OUT_DIR="test-verilator"
ESS_OUT_DIR="test-essent"
DIFF_DIR="test-diff"

mkdir -p $VER_OUT_DIR
mkdir -p $ESS_OUT_DIR
mkdir -p $DIFF_DIR

mismatch=false
for testProg in "$TEST_PROGRAM_DIR"/*
do
    test=$(basename "$testProg")
    echo "$test:"
    # run verilator
    VERILATOR= ./VTile "$testProg" 2>&1 | tee "$VER_OUT_DIR/$test.out" > /dev/null
    if [ -z "$VERILATOR" ]; then
        echo "  Verilator ran"
    fi
    # run essent
    ESSENT= ./top "$testProg" 2>&1 | tee "$ESS_OUT_DIR/$test.out" > /dev/null
    if [ -z "$ESSENT" ]; then
        echo "  ESSENT ran"
    fi
    # diff the results
    diff -w "$VER_OUT_DIR/$test.out" "$ESS_OUT_DIR/$test.out" > "$DIFF_DIR/$test.out"
    if [ -s "$DIFF_DIR/$test.out" ]; then
      echo "Mismatch for $test"
      mismatch=true
    fi
done
echo "Runs complete"

# notify if any mismatches
if [ "$mismatch" == true ]; then
  echo "There were mismatches. Check $DIFF_DIR for non-empty files"
  exit 1
fi
