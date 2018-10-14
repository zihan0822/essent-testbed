CXXFLAGS = -O3 -std=c++11
CLANG_FLAGS = -fno-slp-vectorize -fbracket-depth=1024

UNAME_OS := $(shell uname -s)
ifeq ($(UNAME_OS),Darwin)
	CXXFLAGS += $(CLANG_FLAGS)
endif

INCLUDES = -I../csrc -Iriscv/include -I../firrtl-sig

LIBS = -Lriscv/lib -Wl,-rpath,riscv/lib -lfesvr -lpthread

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.1609.fir'

emulator: emulator.cc TestHarness.h
	$(CXX) $(CXXFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)

INCLUDES_HEAD = -I../csrc -Iriscv-head/include -I../firrtl-sig
LIBS_HEAD = -Lriscv-head/lib -Wl,-rpath,riscv-head/lib -lfesvr -lpthread
emulator-head: emulator-head.cc TestHarness.h
	$(CXX) $(CXXFLAGS) $(INCLUDES_HEAD) emulator-head.cc -o emulator-head $(LIBS_HEAD)
