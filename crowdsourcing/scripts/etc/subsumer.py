from itertools import ifilter
from nltk.corpus import wordnet as wn

import networkx as nx


class Subsumer(object):
    def __init__(self):
        self._activities = set()
        self._activity_verbs = set()
        self._activity_nouns = set()

        self._activity_graph = nx.DiGraph()
        self._parent = {}
        self._noun_graph = self._build_wn_graph('n')
        self._verb_graph = self._build_wn_graph('v')

        nx.write_adjlist(self._noun_graph, 'nouns.graph')
        nx.write_adjlist(self._verb_graph, 'verbs.graph')

        exc = ['entity', 'physical_entity', 'concept']
        self.__excluded = []
        for e in exc:
            self.__excluded.extend(wn.synsets(e))

    def _build_wn_graph(self, pos):
        graph = nx.DiGraph()
        graph.add_nodes_from(wn.all_synsets(pos))

        for synset in wn.all_synsets(pos):
            for neighbor in synset.hypernyms():
                graph.add_edge(neighbor, synset)
            for neighbor in synset.instance_hypernyms():
                graph.add_edge(neighbor, synset)

        return graph

    def _match_simple_pair(self, pos, word1, word2):
        graph = self._graph(pos)

        if word2 in nx.ancestors(graph, word1):
            return word2
        else:
            return None

    def _match_simple(self, pos, word):
        graph, lookup = self._graph(pos), self._lookup(pos)
        if pos == 'v' and word in lookup:
            return word
        ancs = [a for a in nx.ancestors(graph, word) if a not in self.__excluded]

        return next(ifilter(lambda anc: anc in lookup, ancs), None)

    def _match_complex(self, pos, word):
        def match_ancestor(word):
            graph, lookup = self._graph(pos), self._lookup(pos)

            ancs = nx.ancestors(graph, word)
            for anc in ancs:
                descs = set(nx.descendants(graph, anc))
                val = next(ifilter(lambda desc: desc in lookup, descs), None)
                if val:
                    return anc
            return None

        word_tmp = self._match_simple(pos, word)
        if word_tmp:
            return word_tmp
        return match_ancestor(word)

    def _graph(self, pos):
        if pos == 'n':
            return self._noun_graph
        else:
            return self._verb_graph

    def _lookup(self, pos):
        if pos == 'n':
            return self._activity_nouns
        else:
            return self._activity_verbs

    def _verb_similarity(self, verb1, verb2):
        return verb1.lch_similarity(verb2)

    def _noun_similarity(self, noun1, noun2):
        return noun1.lch_similarity(noun2)

    def _activity_similarity(self, pair1, pair2):
        if pair1 is None or pair2 is None:
            return -1000

        w1, w2 = 0.5, 0.5
        v_sim = self._verb_similarity(pair1[0], pair2[0])
        n_sim = self._noun_similarity(pair1[1], pair2[1])

        return (w1 * v_sim + w2 * n_sim) / (w1 + w2)

    def add_activity(self, (v, n)):
        def add(super_activity, (verb, noun)):
            self._activities.add((verb, noun))
            self._activity_nouns.add(noun)
            self._activity_verbs.add(verb)

            if not super_activity:
                self._activity_graph.add_node((verb, noun))
                print 'Created new activity: {0}'.format((verb, noun))
            else:
                verb_lemma = super_activity[0].lemma_names[0]
                noun_lemma = super_activity[1].lemma_names[0]
                if verb_lemma == v and noun_lemma == n:
                    if super_activity in self._parent:
                        super_activity = self._parent[super_activity]

                        self._activity_graph.add_edge(super_activity, (verb, noun))
                        self._parent[(verb, noun)] = super_activity
                        print 'Matched ({0}, {1}) with {2}'.format(verb, noun, super_activity)
                    else:
                        self._activity_graph.add_node((verb, noun))
                        print 'Created new activity: {0}'.format((verb, noun))
                else:
                    self._activity_graph.add_edge(super_activity, (verb, noun))
                    self._parent[(verb, noun)] = super_activity
                    print 'Matched ({0}, {1}) with {2}'.format(verb, noun, super_activity)

        verbs = [s for s in wn.synsets(v, 'v') if v == s.lemma_names[0]]
        nouns = [s for s in wn.synsets(n, 'n') if n == s.lemma_names[0]]
        activities = [(verb, noun) for verb in verbs for noun in nouns]

        super_activities = [self.subsume(p) for p in activities]
        pairs = zip(super_activities, activities)
        activity = max(pairs, key=lambda p: self._activity_similarity(p[0], p[1]))

        if self._activity_similarity(activity[0], activity[1]) == -1000:
            for (super_activity, activity) in pairs:
                add(super_activity, activity)
        else:
            add(activity[0], activity[1])

    def subsume(self, (verb, noun)):
        super_verb = self._match_simple('v', verb)
        if super_verb:
            super_noun = self._match_complex('n', noun)
            if super_noun:
                return (super_verb, super_noun)
            else:
                return None
        return None

subsumer = Subsumer()
subsumer.add_activity(('walk', 'animal'))
subsumer.add_activity(('walk', 'dog'))
subsumer.add_activity(('eat', 'food'))
subsumer.add_activity(('eat', 'spaghetti'))
subsumer.add_activity(('eat', 'peach'))
subsumer.add_activity(('eat', 'apple'))
subsumer.add_activity(('consult', 'patient'))
subsumer.add_activity(('digest', 'food'))
