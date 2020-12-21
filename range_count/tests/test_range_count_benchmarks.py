import pytest
import random

from range_count.src.binary_search import *
from range_count.src.linear_scan import *

def gen_int():
    return random.randint(0, 1000000000000)

x = gen_int()

a10 = [gen_int() for x in range(0, 10)]
a10.sort()

a100 = [gen_int() for x in range(0, 100)]
a100.sort()

a1000 = [gen_int() for x in range(0, 1000)]
a1000.sort()

a10000 = [gen_int() for x in range(0, 10000)]
a10000.sort()

a100000 = [gen_int() for x in range(0, 100000)]
a100000.sort()

a1000000 = [gen_int() for x in range(0, 1000000)]
a1000000.sort()

a10000000 = [gen_int() for x in range(0, 10000000)]
a10000000.sort()

def test_linear_10(benchmark):
    result = benchmark(range_count_linear_scan, a10, x)

def test_binary_10(benchmark):
    result = benchmark(range_count_binary_search, a10, x)

def test_linear_100(benchmark):
    result = benchmark(range_count_linear_scan, a100, x)

def test_binary_100(benchmark):
    result = benchmark(range_count_binary_search, a100, x)

def test_linear_1000(benchmark):
    result = benchmark(range_count_linear_scan, a1000, x)

def test_binary_1000(benchmark):
    result = benchmark(range_count_binary_search, a1000, x)

def test_linear_10000(benchmark):
    result = benchmark(range_count_linear_scan, a10000, x)

def test_binary_10000(benchmark):
    result = benchmark(range_count_binary_search, a10000, x)

def test_linear_100000(benchmark):
    result = benchmark(range_count_linear_scan, a100000, x)

def test_binary_100000(benchmark):
    result = benchmark(range_count_binary_search, a100000, x)

def test_linear_1000000(benchmark):
    result = benchmark(range_count_linear_scan, a1000000, x)

def test_binary_1000000(benchmark):
    result = benchmark(range_count_binary_search, a1000000, x)

def test_linear_10000000(benchmark):
    result = benchmark(range_count_linear_scan, a10000000, x)

def test_binary_10000000(benchmark):
    result = benchmark(range_count_binary_search, a10000000, x)
