package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.Cons;

/**
 * @author Ivan Gavrilović
 */
public class NounDict extends Dict{
    public static NounDict instance = new NounDict();

    private NounDict(){
        wordsKeys = new char[Cons.UNIQUE_NOUNS][];
    }

    public static NounDict getInstance(){
        return instance;
    }
}
