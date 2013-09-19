#!venv/bin/python

from binary_search import binary_search
import redis
import sys

# Replace the next line before deploying
r = redis.StrictRedis(host='localhost', port=6379, db=0)
_prefixes = []


def load_prefixes(filename):
    """Load the list of prefixes from file.

    Args:
        filename: Name of the file which contains all the prefixes.
    """
    global _prefixes
    with open(filename) as f:
        _prefixes = f.readlines()
        _prefixes = map(lambda s: s[0:-1], _prefixes)


def preprocess(r, infile, db='compl'):
    """Preprocess a file and load it into Redis.

    Loads a file and preprocesses it for autocompletion. Namely, we store
    into a Redis key prefixes of words; each complete word is marked as such.

    Args:
        r: Redis instance from which to extract the words.
        infile: File from which to load the words.
        db: The name of the Redis key which to query.
    """
    if not r.exists(db):
        sys.stderr.write('Loading entries into Redis DB')

        # Load only prefixes of length up to 3. This is for performance
        # purposes.
        with open(infile) as f:
            for line in f:
                line = line.strip().lower()
                prefix = line[0:3]
                r.zadd(db, 0, prefix)

                r.zadd(db, 0, line + '*')
    else:
        sys.stderr.write('DB already exists\n')


def complete(r, prefix, count=None, db='compl'):
    """Returns a list of autocompletion suggestions.

    Args:
        r: Redis instance from which to extract the words.
        prefix: A string representing a prefix to be matched.
        count: The number of results to be returned. This is None,
            if all the results are wanted.
        db: The name of the Redis key which to query.

    Returns:
        A list of strings representing possible autocompletion suggestions.
    """
    global _prefixes
    if not _prefixes:
        load_prefixes('static/data/prefixes.txt')

    pos = binary_search(_prefixes, prefix)
    if pos == -1:
        return []

    lo = r.zrank(db, prefix) + 1
    if pos + 1 == len(_prefixes):
        hi = r.dbsize(db)
    else:
        hi = r.zrank(db, _prefixes[pos + 1]) - 1
    length = hi - lo if count is None or hi - lo < count else count

    return map(lambda s: s[0:-1], r.zrange(db, lo, lo + length))
