package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.Cons;

/**
 * @author Ivan Gavrilović
 */
public class VerbDict extends Dict {
    public static VerbDict instance = new VerbDict();

    public VerbDict() {
        wordsKeys = new char[Cons.UNIQUE_VERBS][];
    }

    public static VerbDict getInstance() {
        return instance;
    }
}
