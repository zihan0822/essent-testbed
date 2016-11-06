CFLAGS = -O3 -std=c++11

INCLUDES = -I../csrc

LIBS = -lgmpxx -lgmp

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.fir'

rocketchip: TestHarness-harness.cc TestHarness.h
	g++ $(CFLAGS) $(INCLUDES) TestHarness-harness.cc -o rocketchip $(LIBS)
