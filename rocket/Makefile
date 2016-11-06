CFLAGS = -O3 -std=c++11

INCLUDES = -I../csrc -I/home/sbeamer/riscv/include

LIBS = -lgmpxx -lgmp -L/home/sbeamer/riscv/lib -Wl,-rpath,/home/sbeamer/riscv/lib -lfesvr -lpthread

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.fir'

rocketchip: TestHarness-harness.cc TestHarness.h
	g++ $(CFLAGS) $(INCLUDES) TestHarness-harness.cc -o rocketchip $(LIBS)

emulator: emulator.cc TestHarness.h
	g++ $(CFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)
