# Run Rocket Chip to get .fir (FIRRTL) file of design
generated_dir = $(abspath riscv-mini/generated-src)
fir_filename = Tile.fir
fir_path = $(abspath $(fir_filename))
riscv-mini/README.md:
	git submodule update --init riscv-mini

$(generated_dir)/$(fir_filename): riscv-mini/README.md
	cd riscv-mini; make compile 

$(fir_filename): $(generated_dir)/$(fir_filename)
	cp $(generated_dir)/$(fir_filename) .


Tile.h: $(fir_filename)
	cd ../essent; sbt 'run -O2 $(fir_path)'

CXXFLAGS = -O3 -std=c++11
INCLUDES = -I../firrtl-sig	

top: top.cc mm.cc mm.h Tile.h
	$(CXX) $(CXXFLAGS) $(INCLUDES) top.cc mm.cc -o top 
