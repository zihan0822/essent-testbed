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

riscv/lib/libfesvr.so:
	git submodule update --init riscv-fesvr
	cd riscv-fesvr; git checkout `cat ../fesvr.commit` ; git checkout . ; git clean . -dxf
	patch riscv-fesvr/fesvr/dtm.cc ../riscv-fesvr.patch
	patch riscv-fesvr/fesvr/device.h ../riscv-fesvr_device_h.patch
	mkdir -p $(riscv_dir)
	cd riscv-fesvr; mkdir build; cd build; ../configure --prefix=$(riscv_dir) --target=riscv64-unknown-elf; make install

TestHarness.h:
	cd ../essent; sbt 'run $(FIR_PATH)'

emulator: emulator.cc TestHarness.h riscv/lib/libfesvr.so
	$(CXX) $(CXXFLAGS) $(INCLUDES) emulator.cc -o emulator $(LIBS)
