#include <iostream>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <uint.h> 
#include <stdint.h>
#include <cstring>
#include <queue>
#include <fstream>
#include "Top.h"
using namespace std;

static uint64_t trace_count = 0;
static uint64_t main_time = 0;
Top *top;

void tick(bool verbose, bool done_reset) {
    top->eval(true,verbose,done_reset);

}

void load_mem(UInt<32>* mem, const char* fn) {
  int start = 0;
  ifstream in(fn);
  if (!in) {
    cerr << "could not open " << fn << endl;
    exit(EXIT_FAILURE);
  }
  string line;
  int j = 0;
  
  while (getline(in, line)) {
    #define parse_nibble(c) ((c) >= 'A' ? (c)-'A'+10 : (c)-'0')
    uint32_t temp = 0;
    for (int i = 0; i < 8; i++){
      temp <<= 4;
      temp = temp | (parse_nibble(line[i]));
    }
    mem[j] = UInt<32>(temp);
    j++;
  }
}

int main(int argc, char** argv) {
  int cycles = 5;
  if (argc >= 3){
      cycles = atoi(argv[2]);
  }
  uint64_t timeout = 1000L;
  top = new Top;

  load_mem(top->mem.memory, (const char*)(argv[1])); 
  cout << UInt<32>(top->mem.memory[0]) << endl;

  top->reset = UInt<1>(1);
  cout << "Starting simulation!" << endl;
  for (size_t i = 0; i < 1 ; i++) {
     tick(true, false); 
     printf("PC: %09" PRIx64 ", REG[%2" PRIu64 "] \n", top->cpu.pc.as_single_word(), top->cpu$registers$io_writereg.as_single_word());
  }

  top->reset = UInt<1>(0);
  int j = 0;
  for (size_t i = 0; i < cycles ; i++) {
    tick(true, true); 

    printf("PC: %09" PRIx64 ", REG[%2" PRIu64 "] \n", top->cpu.pc.as_single_word(), top->cpu$registers$io_writereg.as_single_word()); 
  }
  delete top;
  return 0;
}

