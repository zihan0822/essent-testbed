FIR_PATH = $(shell pwd)/freechips.rocketchip.system.DefaultConfig.fir

CXXFLAGS = -O3 -std=c++11
CLANG_FLAGS = -fno-slp-vectorize -fbracket-depth=1024

UNAME_OS := $(shell uname -s)
ifeq ($(UNAME_OS),Darwin)
	CXXFLAGS += $(CLANG_FLAGS)
endif

ifeq ($(ALL_ON),1)
	CXXFLAGS += -DALL_ON
endif

INCLUDES = -Iriscv/include -I../firrtl-sig

LIBS = -Lriscv/lib -Wl,-rpath,riscv/lib -lfesvr -lpthread

riscv_dir := $(shell pwd)/riscv

riscv/lib/libfesvr.a:
	git submodule update --init riscv-isa-sim
	cd riscv-isa-sim; git checkout `cat ../riscv-isa-sim.commit`
	patch riscv-isa-sim/fesvr/dtm.cc ../riscv-fesvr.patch
	patch riscv-isa-sim/riscv/devices.h ../riscv-isa-sim.patch
	mkdir $(riscv_dir)
	cd riscv-isa-sim; mkdir build; cd build; ../configure --prefix=$(riscv_dir) --target=riscv64-unknown-elf; make install

TestHarness.h:
	cd ../essent; sbt 'run $(FIR_PATH)'

emulator: emulator.cc TestHarness.h riscv/lib/libfesvr.a
	$(CXX) $(CXXFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)
