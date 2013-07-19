#!venv/bin/python

from bisect import bisect_left

def binary_search(a, val):
    lo, hi = 0, len(a)
    pos = bisect_left(a, val)

    return (pos if pos != hi and a[pos] == val else -1)