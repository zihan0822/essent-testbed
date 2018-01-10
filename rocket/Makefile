CXXFLAGS = -O3 -std=c++11

INCLUDES = -I../csrc -I$(RISCV)/include -I../firrtl-sig

LIBS = -L$(RISCV)/lib -Wl,-rpath,$(RISCV)/lib -lfesvr -lpthread

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.1609.fir'

emulator: emulator.cc TestHarness.h
	$(CXX) $(CXXFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)
