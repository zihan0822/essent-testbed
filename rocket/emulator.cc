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
// extern bool verbose;
// extern bool done_reset;

void handle_sigterm(int sig) {
  dtm->stop();
}

void tick_dtm(TestHarness *tile) {
  if (done_reset) {
    dtm_t::resp resp_bits;
    resp_bits.resp = tile->SimDTM_1.debug_resp_bits_resp & 0x3;
    resp_bits.data = tile->SimDTM_1.debug_resp_bits_data & 0x00000003ffffffffl;

    dtm->tick(tile->SimDTM_1.debug_req_ready,
              tile->SimDTM_1.debug_resp_valid,
              resp_bits);

    tile->SimDTM_1.debug_resp_ready = dtm->resp_ready();
    tile->SimDTM_1.debug_req_valid = dtm->req_valid();
    tile->SimDTM_1.debug_req_bits_addr = dtm->req_bits().addr;
    tile->SimDTM_1.debug_req_bits_op = dtm->req_bits().op;
    tile->SimDTM_1.debug_req_bits_data = dtm->req_bits().data;

    tile->SimDTM_1.exit = dtm->done() ? (dtm->exit_code() << 1 | 1) : 0;

    // PRINT_SIG(tile->SimDTM_1.debug_req_ready);
    // PRINT_SIG(tile->SimDTM_1.debug_req_valid);
    // PRINT_SIG(tile->SimDTM_1.debug_req_bits_op);
    // PRINT_SIG(tile->SimDTM_1.debug_resp_ready);
    // PRINT_SIG(tile->SimDTM_1.debug_resp_valid);
    // PRINT_SIG(tile->SimDTM_1.debug_resp_bits_resp);
    // PRINT_SIG(tile->SimDTM_1.debug_resp_bits_data);
  } else {
    tile->SimDTM_1.debug_req_valid = 0;
    tile->SimDTM_1.debug_resp_ready = 0;
    tile->SimDTM_1.exit = 0;
  }
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

  done_reset = false;
  tile->reset = 1;
  tick_dtm(tile);
  tile->eval(false);
  // reset for several cycles to handle pipelined reset
  for (int i = 0; i < 10; i++) {
    tile->eval(true);
    tick_dtm(tile);
  }
  tile->reset = 0;
  tile->eval(false);
  tick_dtm(tile);
  done_reset = true;

  while (!dtm->done() && !tile->io_success && trace_count < max_cycles) {
    tile->eval(true);
    tick_dtm(tile);
    trace_count++;
  }

  if (dtm->exit_code()) {
    fprintf(stderr, "*** FAILED *** (code = %d, seed %d) after %llu cycles\n", dtm->exit_code(), random_seed, trace_count);
    ret = dtm->exit_code();
  } else if (trace_count == max_cycles) {
    fprintf(stderr, "*** FAILED *** (timeout, seed %d) after %llu cycles\n", random_seed, trace_count);
    ret = 2;
  } else if (verbose || print_cycles) {
    fprintf(stderr, "Completed after %llu cycles\n", trace_count);
  }

  delete tile;
  delete dtm;

  return ret;
}
