#!venv/bin/python

from bisect import bisect_left

def binary_search(a, val):
    pos = bisect_left(a, val)

    hi = len(a)
    return (pos if pos != hi and a[pos] == val else -1)
