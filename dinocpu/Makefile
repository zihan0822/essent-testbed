fir_filename = Top.fir
generated_dir = $(abspath dinocpu)
fir_path = $(abspath $(fir_filename))

riscv-mini/README.md:
	git submodule update --init riscv-mini

$(generated_dir)/$(fir_filename): 
	cd dinocpu; sbt 'runMain dinocpu.elaborate single-cycle';

$(fir_filename): $(generated_dir)/$(fir_filename)
	cp $(generated_dir)/$(fir_filename) .


Top.h: $(fir_filename)
	cd ../essent; sbt 'run -O2 $(fir_path)'; cd ../dinocpu; rm Top.fir;


CXXFLAGS = -O3 -std=c++11
INCLUDES = -I../firrtl-sig	-I../riscv-mini/riscv-mini/src/main/cc


clean:
	rm Top.h