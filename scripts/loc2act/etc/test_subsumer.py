from nltk.corpus import wordnet as wn
from subsumer import Subsumer

filename = '/home/sebastian/Documents/Internship/Ontology/scripts/loc2act/results/parsed_results.csv'

with open(filename) as f:
    pairs = set()
    for line in f.readlines()[1:]:
        vals = line.split(',')
        pairs.add((vals[0], vals[1]))

def test_lch_similarity():
    sub = Subsumer(lambda x, y: x.lch_similarity(y), -1000)

    ps = [(p1, p2) for p1 in pairs for p2 in pairs if p1 != p2]

    count = 0
    for (p1, p2) in ps:
        res = sub.subsume(p1, p2)
        if res:
            count += 1

    print 'Found {0} matches'.format(count)
    print '{0} / {1}: {2}%'.format(count, len(ps), 100.0 * count / len(ps))

test_lch_similarity()
