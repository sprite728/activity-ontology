#!/usr/bin/python

from nltk.corpus import wordnet as wn
from utils import stemmer, matcher

import collections
import os
import yaml


def normalize(data):
    data = stem(data)
    for d in data:
        d['object'] = matcher(d['object'])
    data = [d for d in data if d['object']]

    return data


def stem(data):
    for d in data:
        d['object'] = stemmer(d['object'])

    return data


def count_votes(data):
    """Count number of Yes and No votes for the 'More' question.

    Args:
        data: List of results.

    Returns:
        The locations for which the number of Yes's is strictly greater
        than the number of No's.
    """
    c = collections.Counter()
    for d in data:
        loc = d['location']
        c[loc] += 1 if d['more'] == 'Yes' else -1

    yes = [loc for loc in c if c[loc] > 0]
    no  = [loc for loc in c if c[loc] <= 0]

    return yes, no


def make_csv(data):
    """Create CSV data from a list of results.

    Args:
        data: List of results.

    Returns:
        A string representing the CSV data.
    """
    data.sort(key=lambda d: (d['location'], d['predicate'], d['object']))

    times = ['morning', 'noon', 'afternoon', 'evening', 'nighttime']
    for d in data:
        if 'time' not in d:
            d['time'] = '|'.join(str(t) for t in times if t in d)

    excluded = ['workerId', 'honeypot']
    keys = [k for k in data[0].keys() if k not in excluded]

    csv_data = [','.join(str(k) for k in keys)]
    for d in data:
        csv_data.append(','.join(str(d[k]) for k in keys))

    return "\n".join(csv_data)


def write_data():
    raw_results = os.path.join(os.pardir, 'results/results.yaml')
    raw_csv = os.path.join(os.pardir, 'results/results4.csv')
    parsed_csv = os.path.join(os.pardir, 'results/parsed_results4.csv')
    stemmed_csv = os.path.join(os.pardir, 'results/stemmed_results4.csv')

    with open(raw_results, 'r') as f:
        raw_data = list(yaml.load_all(f))
    raw_data = [d for d in raw_data
                if 'honeypot' not in d or d['location'] == d['honeypot']]

    with open(raw_csv, 'w') as stream:
        stream.write(make_csv(raw_data))

    with open(stemmed_csv, 'w') as stream:
        stream.write(make_csv(stem(raw_data)))

    with open(parsed_csv, 'w') as stream:
        stream.write(make_csv(normalize(raw_data)))

write_data()
