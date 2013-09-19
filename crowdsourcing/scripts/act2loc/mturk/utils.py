from itertools import islice

def split_every(n, iterable):
    it = iter(iterable)
    piece = list(islice(it, n))
    while piece:
        yield piece
        piece = list(islice(it, n))
