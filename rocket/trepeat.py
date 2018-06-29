#!/usr/bin/env python

import sys
import subprocess
import time

def main():
  if len(sys.argv) < 3:
    print 'python trepeat.py <num iterations> <command>'
    return
  for i in range(int(sys.argv[1])):
    start_time = time.time()
    subprocess.call(sys.argv[2], shell=True)
    end_time = time.time()
    print 'Trial Time: %f' % (end_time - start_time)

if __name__ == '__main__':
  main()
