essent-testbed [![Build Status](https://github.com/ucsc-vama/essent-testbed/actions/workflows/scala-ci.yml/badge.svg)](https://github.com/ucsc-vama/essent-testbed/actions/workflows/scala-ci.yml)
================================================================================

This internal (private) repo is the workspace for essent development. To work on essent, everything needed should either be in this repo, or it should provide the automation to grab the needed resources.


# Small Design Tests
Launch sbt and run the name of small design or say all to try all of them. For example:
```
  $ sbt
  > run all
```
You can peek at the small designs in `src/main/scala`. This execution method uses chisel-testers to run the testbench (in Scala), and it communicates to essent-generated simulators (compiled C++) via inter-process pipes using the interface from Verilator.


# Larger Designs
There are various larger designs in this repo. Each one has a directory and the appropriate submodules and Makefiles to run it.
  + Rocket Chip (rocket16, rocket18, rocket-recent) from different time periods, rocket is deprecated
  + BOOM
  + riscv-mini
  + dinocpu - currently with custom C++ harness
