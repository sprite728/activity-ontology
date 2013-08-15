#!/usr/bin/env python

from matcher import Matcher
from nltk.corpus import wordnet as wn
from normalize_results import stemmer

import os
import yaml


def count_unique(data):
    """Counts the number of unique DBPedia instances in the answer set.

    Args:
        data: List of answers.

    Returns:
        The percentage of unique instances in the file.
    """
    uniq = set(d['object'] for d in data)

    return len(uniq), len(uniq) / float(len(data))


def count_matched(data):
    """Counts the number of DBPedia instances that match with WordNet.

    Also includes partial matches.

    Args:
        data: List of answers.

    Returns:
        The percentage of instances that match.
    """
    objects = set(d['object'] for d in data)
    wrong = []
    for obj in objects:
        repl = Matcher.match(obj)
        if not repl:
            wrong.append(obj)
    correct = len(objects) - len(wrong)

    print wrong
    return correct, correct / float(len(objects))


def count_outliers(data):
    """Count number of words from data that do not match with Google
    1 word Ngrams.

    Args:
        data: List of answers.

    Returns:
        The number of words that do not match, as well as the words themselves
    """
    words_file = '/home/sebastian/Documents/Internship/Ngrams/google-books-common-words.txt'

    with open(words_file) as f:
        words = set(map(lambda s: s.split()[0], f.readlines()))
    objects = set(d['object'] for d in data)

    outliers = []
    for obj in objects:
        word = obj.strip().upper()
        if ' ' not in word and word not in words:
            outliers.append(word)

    return len(outliers), outliers


def print_stats(raw_data):
    unique, p_unique = count_unique(raw_data)
    matched, p_matched = count_matched(raw_data)

    print 'Before Stemming:'
    print '---Unique'
    print "Number: {0}\nPercentage: {1}".format(unique, p_unique)
    print '---Matched'
    print "Number: {0}\nPercentage: {1}".format(matched, p_matched)

    raw_data = stemmer(raw_data)
    unique, p_unique = count_unique(raw_data)
    matched, p_matched = count_matched(raw_data)

    print
    print 'After Stemming:'
    print '---Unique'
    print "Number: {0}\nPercentage: {1}".format(unique, p_unique)
    print '---Matched'
    print "Number: {0}\nPercentage: {1}".format(matched, p_matched)


    c_outliers, outliers = count_outliers(raw_data)
    print
    print 'Outliers:'
    print "Number: {0}\nWords: {1}".format(c_outliers, outliers)


def write_data():
    raw_results = os.path.join(os.pardir, 'results/results.yaml')

    with open(raw_results, 'r') as f:
        raw_data = list(yaml.load_all(f))
        print_stats(raw_data)

write_data()
