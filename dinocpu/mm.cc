#include "mm.h"
#include <iostream>
#include <fstream>
#include <cstdlib>
#include <cstring>
#include <string>
#include <cassert>

mm_magic_t::mm_magic_t(size_t size, size_t word_size):
  data(new char[size]),
  size(size),
  word_size(word_size)
{
}

mm_magic_t::~mm_magic_t()
{
  delete [] data;
}


