package io.mem0r1es.activitysubsumer.wordnet;

import java.util.HashMap;

/**
 * @author Ivan Gavrilović
 */
public class NounDict implements Dict{
    public static NounDict instance = new NounDict();
    private int nextId = -1;
    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>(120000);
    private HashMap<Integer, String> inverse = new HashMap<Integer, String>(120000);

    public static NounDict getInstance(){
        return instance;
    }

    public int put(String s){
        if (dictionary.containsKey(s)) return dictionary.get(s);
        nextId++;

        dictionary.put(s, nextId);
        inverse.put(nextId, s);
        return nextId;
    }

    public int get(String s){
        return dictionary.get(s);
    }

    public String get(int code){
        return inverse.get(code);
    }
}
