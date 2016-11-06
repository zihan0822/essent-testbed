#include <fesvr/dtm.h>
#include <iostream>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "comm_wrapper.h"
#include "TestHarness.h"

dtm_t* dtm;
static uint64_t trace_count = 0;
bool verbose;
bool done_reset;

void handle_sigterm(int sig) {
  dtm->stop();
}

int main(int argc, char** argv) {
  unsigned random_seed = (unsigned)time(NULL) ^ (unsigned)getpid();
  uint64_t max_cycles = -1;
  uint64_t start = 0;
  int ret = 0;
  bool print_cycles = false;

  for (int i = 1; i < argc; i++) {
    std::string arg = argv[i];
    if (arg.substr(0, 2) == "-s")
      random_seed = atoi(argv[i]+2);
    else if (arg == "+verbose")
      verbose = true;
    else if (arg.substr(0, 12) == "+max-cycles=")
      max_cycles = atoll(argv[i]+12);
    else if (arg.substr(0, 7) == "+start=")
      start = atoll(argv[i]+7);
    else if (arg.substr(0, 12) == "+cycle-count")
      print_cycles = true;
  }

  srand(random_seed);
  srand48(random_seed);

  TestHarness *tile = new TestHarness;

  dtm = new dtm_t(std::vector<std::string>(argv + 1, argv + argc));

  signal(SIGTERM, handle_sigterm);

  // reset for several cycles to handle pipelined reset
  for (int i = 0; i < 10; i++) {
    tile->reset = 1;
    tile->eval(true);
    tile->reset = 0;
  }
  done_reset = true;

  while (!dtm->done() && !tile->io_success && trace_count < max_cycles) {
    tile->eval(true);
    trace_count++;
  }

  if (dtm->exit_code()) {
    fprintf(stderr, "*** FAILED *** (code = %d, seed %d) after %ld cycles\n", dtm->exit_code(), random_seed, trace_count);
    ret = dtm->exit_code();
  } else if (trace_count == max_cycles) {
    fprintf(stderr, "*** FAILED *** (timeout, seed %d) after %ld cycles\n", random_seed, trace_count);
    ret = 2;
  } else if (verbose || print_cycles) {
    fprintf(stderr, "Completed after %ld cycles\n", trace_count);
  }

  delete tile;

  return ret;
}
