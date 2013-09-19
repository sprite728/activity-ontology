import collections

def print_ranking(ranking):
    for location, acts in ranking.items():
        print '{0:<30} {1}'.format(location, acts)

with open('../results/parsed_results2.csv') as f:
    lines = f.readlines()[1:]

    activity_dict = collections.defaultdict(collections.Counter)
    for line in lines:
        pred, obj, loc = line.split(',')[:3]
        activity_dict[loc].update([(pred, obj)])

    print_ranking(activity_dict)
