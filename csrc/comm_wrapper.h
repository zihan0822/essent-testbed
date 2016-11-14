#ifndef COMM_WRAPPER_H_
#define COMM_WRAPPER_H_

#include <cinttypes>
#include <string>
#include <gmpxx.h>

#include "sim_api.h"

#define PRINT_SIG(sig_name) printf("%s %0llx\n", #sig_name, sig_name)


mpz_class fromUInt(uint64_t x) {
  return mpz_class(static_cast<unsigned long>(x));
}

bool verbose = false;
bool done_reset = true;

class sig_wrapper_t {
public:
  sig_wrapper_t() {}
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

class big_wrapper_t : public sig_wrapper_t {
public:
  big_wrapper_t(mpz_class *bi, int bit_width) : bi_(bi),
    bit_width_(bit_width) {}

  virtual size_t get_num_words() const { return (bit_width_ + 64 - 1) / 64; }
  virtual size_t get_value(uint64_t* values) {
    mpz_class temp = *bi_;
    for (int i=0; i < get_num_words(); i++) {
      values[i] = temp.get_ui();
      temp = temp >> 64;
    }
    return get_num_words();
  }
  virtual size_t put_value(uint64_t* values) {
    *bi_ = 0;
    const int num_words = get_num_words();
    for (int i=0; i < num_words; i++) {
      *bi_ = *bi_ << 64;
      *bi_ = *bi_ + fromUInt(values[num_words-i-1]);
    }
    return get_num_words();
  }
private:
  mpz_class *bi_;
  const int bit_width_;
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

  void add_in_signal(mpz_class *sig_ptr, int bit_width) {
    sim_data.inputs.push_back(new big_wrapper_t(sig_ptr, bit_width));
  }

  void add_out_signal(uint64_t *sig_ptr) {
    sim_data.outputs.push_back(new sig_wrapper_t(sig_ptr));
  }

  void add_out_signal(mpz_class *sig_ptr, int bit_width) {
    sim_data.outputs.push_back(new big_wrapper_t(sig_ptr, bit_width));
  }

  void add_signal(uint64_t *sig_ptr) {
    sim_data.signals.push_back(new sig_wrapper_t(sig_ptr));
  }

  void add_signal(mpz_class *sig_ptr, int bit_width) {
    sim_data.signals.push_back(new big_wrapper_t(sig_ptr, bit_width));
  }

  void map_signal(std::string label, size_t index) {
    sim_data.signal_map[label] = index;
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
