import collections
import re
import string

class SpellChecker(object):
    """Basic spell checker following Peter Norvig's post:
                                http://norvig.com/spell-correct.html

    Only works for words that are edit distance 2 from the given word.
    """
    def __init__(self, filename):
        def words(text):
            return re.findall('[a-z]+', text.lower())

        with open(filename) as f:
            self.NWORDS = self._train(words(f.read()))

    def _train(self, features):
        """Train a probability model from a list of features."""
        model = collections.defaultdict(lambda: 1)
        for f in features:
            model[f] += 1
        return model

    def _edits1(self, word):
        """Return all words that are edit distance 1 from the given word.

        Args:
            word: input word

        Returns:
            A set of all words that are edit distance 1 from *word*
        """
        alphabet = string.ascii_lowercase

        splits = [(word[:i], word[i:]) for i in range(len(word) + 1)]
        deletes = [a + b[1:] for a, b in splits if b]
        transposes = [a + b[1] + b[0] + b[2:] for a, b in splits if len(b) > 1]
        replaces = [a + c + b[1:] for a, b in splits for c in alphabet if b]
        inserts = [a + c + b for a, b in splits for c in alphabet]

        return set(deletes + transposes + replaces + inserts)

    def _known_edits2(self, word):
        """Return all words that are edit distance 2 from the given word.

        Args:
            word: input word

        Returns:
            A set of all words that exist in the dictionary and are edit
                distance 2 from *word*
        """
        return set(e2 for e1 in self._edits1(word) for e2 in self._edits1(e1) \
                if e2 in self.NWORDS)

    def _known(self, words):
        """Return a set of known words (words in the dictionary) given a list
                of words.
        """
        return set(w for w in words if w in self.NWORDS)

    def correct(self, word):
        """Try to correct the spelling of word.

        The word is preferred to all words edit distance 1 from it, which are
        in turn preferred to all words edit distance 2 from it. If no words
        match, the initial word is returned.

        Words are ranked based on the probability model trained above.

        Args:
            word: word to be spell-checked

        Returns:
            The best candidate for correction.
        """
        candidates = self._known([word]) or self._known(self._edits1(word)) \
                or self._known(self._known_edits2(word)) or [word]

        return max(candidates, key=self.NWORDS.get)
