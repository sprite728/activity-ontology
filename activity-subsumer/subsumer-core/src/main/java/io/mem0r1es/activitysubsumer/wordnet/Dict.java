package io.mem0r1es.activitysubsumer.wordnet;

/**
 * @author Ivan Gavrilović
 */
public interface Dict {
    public int put(String s);

    public int get(String s);

    public String get(int code);
}
