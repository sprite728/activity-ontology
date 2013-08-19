from matcher import Matcher

import inflect
import re

def stemmer(word):
    exclude = ['bus', 'gas', 'tennis', 'canvas', 'campus', 'mathematics',
        'clothes', 'theives']
    engine = inflect.engine()

    p = re.compile('^(a |the )(.*)')
    q = re.compile('.*ss$')
    m1 = p.match(word)
    if m1:
        word = m1.group(2)

    m2 = q.match(word)
    if word not in exclude and not m2:
        try:
            w = engine.singular_noun(word)
            word = w if w else word
        except:
            pass

    return word


def matcher(word):
    repl = Matcher.match(word)
    if repl:
        return repl
    return word
