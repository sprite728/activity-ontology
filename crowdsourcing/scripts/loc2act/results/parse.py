import collections


def parse(filename):
    with open(filename) as f:
        act_map = {}
        loc_map = collections.defaultdict(set)

        for line in f:
            vals = line.split(',')
            act = vals[0] + ',' + vals[1]
            loc = vals[2]
            time = set(vals[3].split('|'))
            avg = vals[4]

            if act in act_map:
                act_map[act]['locs'].add(loc)
                act_map[act]['time'] |= time
                act_map[act]['avg'][avg] += 1
            else:
                act_map[act] = {}
                act_map[act]['locs'] = set([loc])
                act_map[act]['time'] = time
                act_map[act]['avg'] = collections.Counter({avg: 1})

            loc_map[loc].add(act)

    return act_map, loc_map

def write():
    act_map, loc_map = parse('parsed_results4.csv')

    with open('activities.graph', 'w') as f:
        for act, vals in act_map.items():
            locs = '|'.join(vals['locs'])
            times = '|'.join(vals['time'])
            avg = max(vals['avg'], key=lambda v: vals['avg'][v])
            f.write(','.join([act, locs, times, avg]))
            f.write('\n')

    with open('locations.graph', 'w') as f:
        for loc, vals in loc_map.items():
            acts = '|'.join(vals)
            f.write(','.join([loc, acts]))
            f.write('\n')

write()
