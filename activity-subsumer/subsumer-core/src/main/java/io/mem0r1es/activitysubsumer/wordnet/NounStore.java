package io.mem0r1es.activitysubsumer.wordnet;

/**
 * @author Ivan Gavrilović
 */
public class NounStore extends SynsetStore {
    private static NounStore instance = null;

    public NounStore(int numWords, int parChldRels) {
        super(numWords, parChldRels);
        instance = this;
    }

    public static NounStore getInstance(){
        if (instance == null) throw new RuntimeException("Initialize first");
        return instance;
    }
}
