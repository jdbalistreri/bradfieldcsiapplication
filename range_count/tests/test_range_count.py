import pytest

from range_count.src.binary_search import *
from range_count.src.linear_scan import *


test_cases = [
        {"a": [], "x": 1, "expected": 0},
        {"a": [0, 4, 7], "x": 1, "expected": 0},
        {"a": [0, 1, 4, 7], "x": 1, "expected": 1},
        {"a": [0, 1, 4, 7], "x": -10, "expected": 0},
        {"a": [0, 1, 1, 1, 1, 4, 7], "x": 1, "expected": 4},
        {"a": [0, 1, 2, 3, 4, 4], "x": 4, "expected": 2},
        {"a": [0, 0, 0], "x": 0, "expected": 3},
        {"a": [-3, -2, -1], "x": -1, "expected": 1},
    ]

def test_range_count():
    for case in test_cases:
        a = case["a"]
        x = case["x"]
        expected = case["expected"]

        assert range_count_linear_scan(a, x) == expected
        assert range_count_binary_search(a, x) == expected
