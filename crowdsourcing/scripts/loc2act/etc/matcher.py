from nltk.corpus import wordnet as wn
from nltk.metrics import edit_distance
from spell_checker import SpellChecker

import re

class Matcher(object):
    """Class that matches DBPedia instances to WordNet word."""
    checker = SpellChecker('/home/sebastian/Documents/Internship/Ngrams/spelling.txt')

    @staticmethod
    def match(orig):
        """Try to match a DBPedia instance with a WordNet word.

        Args:
            orig: word representing the DBPedia instance to match

        Returns:
            The first match found, or '' if no matches
        """
        # Remove non-alphabetic, non-space characters from word
        word = re.sub(r"[^A-Za-z-\s]", "", orig).strip()

        repl = Matcher.__match(word, wn.NOUN)
        if repl:
            return repl

        # Try matching constructs 'X of the Y', 'X of Y', 'X for Y',
        # or 'X with a Y'
        opts = 'of the|of|with a|for'
        p = re.compile('^(\w+) (' + opts + ') (\w+)$', re.IGNORECASE)
        m = p.match(word)
        if m:
            repl = Matcher.__match(m.group(1), wn.NOUN)
            if repl:
                return repl

        # Try matching constructs 'X Y', where X is a present participle
        p = re.compile('^(\w+)ing (\w+)$', re.IGNORECASE)
        m = p.match(word)
        if m:
            repl = Matcher.__match(m.group(2), wn.NOUN)
            if repl:
                return repl

        # Try matching constructs 'X Y' where X is an adjective
        p = re.compile('^([\w-]+) (\w+)$', re.IGNORECASE)
        m = p.match(word)
        if m:
            adj = Matcher.__match(m.group(1), wn.ADJ)
            repl = Matcher.__match(m.group(2), wn.NOUN)
            if adj and repl:
                return repl

        # Try matching constructs 'X Y' where X does not match anything
        # and Y is a noun
        p = re.compile('^([\w-]+) (\w+)$', re.IGNORECASE)
        m = p.match(word)
        if m:
            repl = Matcher.__match(m.group(2), wn.NOUN)
            if repl:
                return repl

        # Try matching past participles
        p = re.compile('^(\w+)ed$', re.IGNORECASE)
        m = p.match(word)
        if m:
            repl = Matcher.__match(word, wn.VERB)
            if repl:
                return repl

        # Try to correct spelling and match
        if ' ' not in word:
            word = Matcher.checker.correct(word)
        repl = Matcher.__match(word, wn.NOUN)
        if repl:
            return repl

        return ''

    @staticmethod
    def __match(word, typ):
        # Try matching the base word
        try:
            synsets = wn.synsets(word)
            synset = min(synsets, key=lambda w: edit_distance(w, word))

            return synset.lemma_names[0]
        except:
            repl = wn.morphy(word, typ)

        return repl
