#!venv/bin/python

import redis
import sys

# Replace the next line before deploying

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
        sys.stderr.write('Loading entries into Redis DB\n')

        # Load only prefixes of length up to 3. This is for performance
        # purposes.
        for line in infile.readlines():
            line = line.strip()
            for i in xrange(1, 4):
                prefix = line[0:i]
                r.zadd(db, 0, prefix)

            r.zadd(db, 0, line + '*')
    else:
        sys.stderr.write('DB already exists\n')

def complete(r, prefix, count=None, db='compl'):
    """Returns a list of autocompletion suggestions.

    Args:
        r: Redis instance from which to extract the words.
        prefix: A string representing a prefix to be matched.
        count: The number of results to be returned. This is set to None, 
            if all the results are wanted.
        db: The name of the Redis key which to query.

    Returns:
        A list of strings representing possible autocompletion suggestions.
    """
    results = []
    rangelen = 50

    start = r.zrank(db, prefix)
    if not start:
        return []

    while len(results) != count:
        ranges = r.zrange(db, start, start + rangelen - 1)
        start += rangelen

        if not ranges or not len(ranges):
            break

        for entry in ranges:
            minlen = min(len(entry), len(prefix))
            if entry[0:minlen] != prefix[0:minlen]:
                count = len(results)
                break
            
            if entry[-1] == '*' and len(results) != count:
                results.append(entry[0:-1])

    return results