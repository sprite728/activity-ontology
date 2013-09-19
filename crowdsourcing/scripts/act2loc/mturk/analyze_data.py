import collections


with open('../etc/locs.txt') as f:
    def parse_string(loc):
        __special = {'rv_park': 'RV park', 'atm': 'ATM'}
        if loc in __special:
            loc = __special[loc]
        else:
            loc = loc.replace('_', ' ').capitalize()

        return loc

    locations = map(lambda s: parse_string(s.strip()), f.readlines())

def read_stats(filename):
    with open(filename) as f:
        occurences = collections.defaultdict(collections.Counter)

        for line in f:
            values = line.split(',')
            activity = values[0]
            rest = filter(lambda s: s != 'None', values[1:-2])

            for location in rest:
                occurences[activity][location] += 1

    return occurences

fst_exp = read_stats('results_first.csv')
scd_exp = read_stats('results_second.csv')

fst_dict = {}
scd_dict = {}

print 'First Experiment'
print '-------------------------'
for activity, counter in fst_exp.items():
    none = counter.keys()
    at_most_twice = [c for c in counter if counter[c] <= 2]
    locs = list((set(locations) - set(none)) | set(at_most_twice))

    locs.sort(key=lambda l: 5 - counter[l], reverse=True)
    scores = [5 - counter[l] for l in locs]
    locs = zip(locs, scores)

    print '{0:<30} {1}'.format(activity, locs)

    locs = dict(locs)
    fst_dict[activity] = locs

print '============================================================'
print 'Second Experiment'
print '-------------------------'
for activity, counter in scd_exp.items():
    locs = [c for c in counter if counter[c] >= 3]

    locs.sort(key=lambda l: counter[l], reverse=True)
    scores = [counter[l] for l in locs]
    locs = zip(locs, scores)

    print '{0:<30} {1}'.format(activity, locs)

    locs = dict(locs)
    scd_dict[activity] = locs

print '============================================================'
for activity in scd_exp.keys():
    fst_score = fst_dict[activity]
    scd_score = scd_dict[activity]

    score = {}
    for location in locations:
        fst, scd = 0, 0
        if location in fst_score:
            fst = fst_score[location]
        if location in scd_score:
            scd = scd_score[location]

        score[location] = (fst + 2 * scd) / (1.0 + 2.0)
    locs = sorted([(l, '%.2g'%(score[l])) for l in score if score[l] != 0], key=lambda p: p[1], reverse=True)
    more_three = [l[0] for l in locs if float(l[1]) >= 3]

    print '{0:<30} {1}'.format(activity, more_three)
    print '-------'
