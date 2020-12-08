#ifndef MM_EMULATOR_H
#define MM_EMULATOR_H

#include <stdint.h>
#include <cstring>
#include <queue>
#include <uint.h>


class mm_magic_t
{
 public:
  mm_magic_t(size_t size, size_t word_size);
  ~mm_magic_t();
  void init(size_t sz, int word_size);
  char* get_data() { return data; }


  void write(uint64_t addr, char *data);
  void write(uint64_t addr, char *data, uint64_t strb, uint64_t size);
  std::vector<char> read(uint64_t addr);

 private:
  char* data;
  size_t size;
  size_t word_size;

};

void load_mem(char* mem, const char* fn);

#endif