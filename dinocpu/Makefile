fir_filename = Top.fir
generated_dir = $(abspath dinocpu)
fir_path = $(abspath $(fir_filename))

$(generated_dir)/$(fir_filename): 
	cd dinocpu; sbt 'runMain dinocpu.elaborate single-cycle';

$(fir_filename): $(generated_dir)/$(fir_filename)
	cp $(generated_dir)/$(fir_filename) .


Top.h: $(fir_filename)
	cd ../essent; sbt 'run -O2 $(fir_path)'; cd ../dinocpu; rm Top.fir;


CXXFLAGS = -O3 -std=c++11 -g
INCLUDES = -I../firrtl-sig 

top: top.cc mm.cc
	$(CXX) $(CXXFLAGS) $(INCLUDES) top.cc -o top 
clean:
	rm Top.h top Top.fir