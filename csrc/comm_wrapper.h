#ifndef COMM_WRAPPER_H_
#define COMM_WRAPPER_H_

#include <cinttypes>

#include "sim_api.h"


class sig_wrapper_t {
public:
  sig_wrapper_t(uint64_t *x) : x_(x) {}

  virtual size_t get_num_words() { return 1; }
  virtual size_t get_value(uint64_t* values) {
    *values = *x_;
    return 1;
  }
  virtual size_t put_value(uint64_t* values) {
    *x_ = *values;
    return 1;
  }
private:
  uint64_t *x_;
};


template<typename DUT_t_>
class CommWrapper: public sim_api_t<sig_wrapper_t*> {
public:
  CommWrapper(DUT_t_ &dut) : dut_(dut), ok_to_exit_(false) {}

  ~CommWrapper() {
    for (int i=0; i<sim_data.inputs.size(); i++)
      delete sim_data.inputs[i];
    for (int i=0; i<sim_data.outputs.size(); i++)
      delete sim_data.outputs[i];
    for (int i=0; i<sim_data.signals.size(); i++)
      delete sim_data.signals[i];
  }

  bool done() {
    return ok_to_exit_;
  }

  void init_sim_data() {
    sim_data.inputs.clear();
    sim_data.outputs.clear();
    sim_data.signals.clear();
  }

  void add_in_signal(uint64_t *sig_ptr) {
    sim_data.inputs.push_back(new sig_wrapper_t(sig_ptr));
  }

  void add_out_signal(uint64_t *sig_ptr) {
    sim_data.outputs.push_back(new sig_wrapper_t(sig_ptr));
  }

  void add_signal(uint64_t *sig_ptr) {
    sim_data.signals.push_back(new sig_wrapper_t(sig_ptr));
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
    dut_.eval(true);
  }
  virtual void update() {
    dut_.eval(false);
  }
  virtual size_t put_value(sig_wrapper_t*& sig, uint64_t* data,
                           bool force = false) {
    return sig->put_value(data);
  }
  virtual size_t get_value(sig_wrapper_t*& sig, uint64_t* data) {
    return sig->get_value(data);
  }
  virtual size_t get_chunk(sig_wrapper_t*& sig) {
    return sig->get_num_words();
  }
};

#endif  // SOLOREG_H_
