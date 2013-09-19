from nltk.corpus import wordnet as wn

import collections

class Subsumer(object):
    def __init__(self, similarity, threshold):
        self.similarity = similarity
        self.threshold = threshold

        self._excluded_cache = collections.defaultdict(list)

    def _excluded(self, word):
        if word not in self._excluded_cache:
            self._excluded_cache[word] = word.root_hypernyms()

        return self._excluded_cache[word]

    def _similarity_score(self, (verb1, noun1), (verb2, noun2)):
        score_verb = self.similarity(verb1, verb2)
        score_noun = self.similarity(noun1, noun2)

        w1, w2 = 0.5, 0.5
        score = (w1 * score_verb + w2 * score_noun) / (w1 + w2)

        return score

    def _find_lsa(self, word1, word2):
        hypernyms = word1.lowest_common_hypernyms(word2)
        excluded = self._excluded(word1)

        return [w for w in hypernyms if w not in excluded]

    def subsume(self, (verb1, noun1), (verb2, noun2)):
        try:
            verbs1 = wn.synsets(verb1, 'v')
            nouns1 = wn.synsets(noun1, 'n')
            verbs2 = wn.synsets(verb2, 'v')
            nouns2 = wn.synsets(noun2, 'n')

            pairs1 = zip(verbs1, nouns1)
            pairs2 = zip(verbs2, nouns2)
        except:
            return []

        results = []
        for ((verb1, noun1), (verb2, noun2)) in zip(pairs1, pairs2):
            score = self._similarity_score((verb1, noun1), (verb2, noun2))
            if score >= self.threshold:
                verbs = self._find_lsa(verb1, verb2)
                nouns = self._find_lsa(noun1, noun2)

                results.extend([(x, y) for x in verbs for y in nouns])

        return results
