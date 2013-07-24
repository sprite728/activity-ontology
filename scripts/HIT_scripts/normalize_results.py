#!/usr/bin/python

# Replaces all verbs in the result data with the first verb in their synset

from nltk.corpus import wordnet as wn

import re
import yaml

try:
    from yaml import CLoader as Loader, CDumper as Dumper
except ImportError:
    from yaml import Loader, Dumper

with open('results/results.yaml', 'r') as f:
    init_data = list(yaml.load_all(f))

predicates = map(lambda datum: datum['predicate'], init_data)

for i, predicate in enumerate(predicates):
    word = predicate.replace(' ', '_') + '.v.01'
    lemmas = wn.synset(word).lemma_names

    predicates[i] = lemmas[0]

for i, datum in enumerate(init_data):
    datum['predicate'] = predicates[i]

stream = file('results/parsed_results.yaml', 'w')
yaml.dump_all(init_data, stream)
