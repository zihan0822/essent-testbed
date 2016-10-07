#ifndef COMM_WRAPPER_H_
#define COMM_WRAPPER_H_
#include "sim_api.h"


class sig_wrapper_t {
public:
  sig_wrapper_t(int x) : x_(x) {}

  virtual size_t get_num_words() { return 1; }
private:
  int x_;
};


template<typename DUT_t_>
class CommWrapper: public sim_api_t<sig_wrapper_t*> {
public:
  CommWrapper(DUT_t_ &dut) : dut_(dut), ok_to_exit_(false) {}

  bool done() {
    return ok_to_exit_;
  }

  void init_sim_data() {
    sim_data.inputs.clear();
    sim_data.outputs.clear();
    sim_data.signals.clear();
  }

private:
  DUT_t_ &dut_;
  bool ok_to_exit_;

  virtual void reset() {
    dut_.reset = 1;
    step();
  }

  virtual void start() {
    dut_.reset = 0;
  }

  virtual void finish() {
    ok_to_exit_ = true;
  }

  virtual void step() {
    // toggle clock
    dut_.eval();
  }
  virtual void update() {}
  virtual size_t put_value(sig_wrapper_t*& sig, uint64_t* data,
                           bool force = false) {
    return 0;
  }
  virtual size_t get_value(sig_wrapper_t*& sig, uint64_t* data) {
    return 0;
  }
  virtual size_t get_chunk(sig_wrapper_t*& sig) {
    return 0;
  }
};

#endif  // SOLOREG_H_
