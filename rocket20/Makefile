FIR_PATH = $(shell pwd)/freechips.rocketchip.system.DefaultConfig.fir

include ../emulator.mk

# override prior rule to get patch for riscv-fesvr
riscv/lib/libfesvr.so:
	git submodule update --init riscv-fesvr
	cd riscv-fesvr; git checkout `cat ../fesvr.commit`
	patch riscv-fesvr/fesvr/dtm.cc riscv-fesvr.patch
	mkdir $(riscv_dir)
	cd riscv-fesvr; mkdir build; cd build; ../configure --prefix=$(riscv_dir) --target=riscv64-unknown-elf; make install
