import scipy
import scipy.stats
import sys

def read_file(filename, field):
  return [float(l.strip().split()[-1]) for l in open(filename) if field in l]

def print_stats(name, x):
  print '%s:' % name
  print ' %f-%f %f(%f)' % (min(x), max(x), scipy.mean(x), scipy.std(x))


filenameA = sys.argv[1]
filenameB = sys.argv[2]
field = sys.argv[3]

valuesA = read_file(filenameA, field)
valuesB = read_file(filenameB, field)

print_stats(filenameA, valuesA)
print_stats(filenameB, valuesB)

meanA = scipy.mean(valuesA)
meanB = scipy.mean(valuesB)

print '%s is %.3f%% %s than %s' % (filenameA, 100*(abs(meanA-meanB))/meanA,
'faster' if meanA < meanB else 'slower', filenameB)

print 't-test:', scipy.stats.ttest_ind(valuesA,valuesB,equal_var=False)
