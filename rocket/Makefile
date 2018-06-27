CXXFLAGS = -O3 -std=c++11 $(CLANG_FLAGS)
CLANG_FLAGS = -fno-slp-vectorize -fbracket-depth=1024

UNAME_OS := $(shell uname -s)
ifeq ($(UNAME_OS),Darwin)
	CXX += $(CLANG_FLAGS)
endif

INCLUDES = -I../csrc -I$(RISCV)/include -I../firrtl-sig

LIBS = -L$(RISCV)/lib -Wl,-rpath,$(RISCV)/lib -lfesvr -lpthread

TestHarness.h:
	cd ../essent; sbt 'run ../rocket/TestHarness.DefaultConfig.1609.fir'

emulator: emulator.cc TestHarness.h
	$(CXX) $(CXXFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)

INCLUDES_HEAD = -I../csrc -I$(RISCV)-head/include -I../firrtl-sig
LIBS_HEAD = -L$(RISCV)-head/lib -Wl,-rpath,$(RISCV)-head/lib -lfesvr -lpthread
emulator-head: emulator-head.cc TestHarness.h
	$(CXX) $(CXXFLAGS) $(INCLUDES_HEAD) emulator-head.cc -o emulator-head $(LIBS_HEAD)
