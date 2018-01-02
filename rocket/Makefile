CFLAGS = -O3 -std=c++11 -fbracket-depth=1024

INCLUDES = -I../csrc -I$(RISCV)/include -I../firrtl-sig

LIBS = -L$(RISCV)/lib -Wl,-rpath,$(RISCV)/lib -lfesvr -lpthread

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.1609.fir'

rocketchip: TestHarness-harness.cc TestHarness.h
	g++ $(CFLAGS) $(INCLUDES) TestHarness-harness.cc -o rocketchip $(LIBS)

emulator: emulator.cc TestHarness.h
	g++ $(CFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)
