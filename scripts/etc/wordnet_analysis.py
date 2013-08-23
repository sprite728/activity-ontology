#!/usr/bin/env python

from nltk.corpus import wordnet as wn

def longest_path():
    s_max = max(wn.all_synsets(), key=lambda s: s.max_depth())
    s_min = max(wn.all_synsets(), key=lambda s: s.min_depth())

    return s_max, s_min


def average_path():
    d_max, d_min = 0, 0
    n = 0
    for synset in wn.all_synsets():
        d_max += synset.max_depth()
        d_min += synset.min_depth()
        n += 1
    n = float(n)

    return d_max / n, d_min / n


def branch_factor(leaf=True):
    f_hypo, f_holo = 0, 0
    n = 0
    for synset in wn.all_synsets():
        hyponyms = synset.hyponyms() + synset.instance_hyponyms()
        holonyms = hyponyms + synset.member_holonyms() +\
                    synset.substance_holonyms() + synset.part_holonyms()

        if hyponyms or leaf:
            f_hypo += len(hyponyms)
            f_holo += len(holonyms)
            n += 1
    n = float(n)

    return f_hypo / n, f_holo / n


def stats():
    s_max, s_min = longest_path()
    max_avg, min_avg = average_path()
    f_hypo_l, f_holo_l = branch_factor()
    f_hypo, f_holo = branch_factor(False)

    print '---Statistics'
    print 'Maximum paths:'
    print '    - by max_depth(): {0} at {1}'.format(s_max, s_max.max_depth())
    print '    - by min_depth(): {0} at {1}'.format(s_min, s_min.min_depth())
    print
    print 'Average paths:'
    print '    - by max_depth(): {0}'.format(max_avg)
    print '    - by min_depth(): {0}'.format(min_avg)
    print
    print 'Average branch factor (with leaves):'
    print '    - only hyponyms: {0}'.format(f_hypo_l)
    print '    - with holonyms: {0}'.format(f_holo_l)
    print 'Average branch factor (excluding leaves):'
    print '    - only hyponyms: {0}'.format(f_hypo)
    print '    - with holonyms: {0}'.format(f_holo)


stats()
