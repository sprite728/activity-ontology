package io.mem0r1es.activitysubsumer.wordnet;

/**
 * @author Ivan Gavrilović
 */
public class VerbStore extends SynsetStore{
    private static VerbStore instance = null;

    public VerbStore(int numWords, int parChldRels) {
        super(numWords, parChldRels);
        instance = this;
    }

    public static VerbStore getInstance() {
        if (instance == null) throw new RuntimeException("Initialize first");
        return instance;
    }
}
