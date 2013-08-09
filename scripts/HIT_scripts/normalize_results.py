#!/usr/bin/python

from nltk.corpus import wordnet as wn
import collections
import yaml


def normalize(data):
    """Replace predicates and objects by synonyms.

    Collapses predicates that are synonyms of each other in one word. For
    instance, 'talk' and 'speak' are replaced with 'talk'.

    Also performs the collapse for objects.

    Args:
        data: List of results each of which contains a predicate-object pair

    Returns:
        A list where the replacement has been performed for each result.
    """
    predicates = map(lambda datum: datum['predicate'], data)

    for i, predicate in enumerate(predicates):
        word = predicate.replace(' ', '_') + '.v.01'

        lemmas = wn.synset(word).lemma_names

        predicates[i] = lemmas[0]

    objects = map(lambda datum: datum['object'], data)
    for i, obj in enumerate(objects):
        word = obj.replace(' ', '_') + '.n.01'

        try:
            lemmas = wn.synset(word).lemma_names
            objects[i] = lemmas[0]
        except:
            pass

    for i, datum in enumerate(data):
        datum['predicate'] = predicates[i]
        datum['object'] = objects[i]

    return data


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

    return [loc for loc in c if c[loc] > 0]


with open('results/results.yaml', 'r') as f:
    raw_data = list(yaml.load_all(f))

with open('results/parsed_results.csv', 'w') as stream:
    stream.write(make_csv(normalize(raw_data)))

with open('results/results.csv', 'w') as stream:
    stream.write(make_csv(raw_data))
