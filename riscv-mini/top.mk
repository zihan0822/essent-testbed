CXXFLAGS = -O3 -std=c++11
CLANG_FLAGS = -fno-slp-vectorize -fbracket-depth=1024
INCLUDES = -I../../riscv-mini/generated-src -Iriscv/include -I../firrtl-sig	

harness: top.cc mm.cc mm.h 
	$(CXX) $(CXXFLAGS) $(INCLUDES) top.cc mm.cc -o top 