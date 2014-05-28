package io.mem0r1es.activitysubsumer.graphs;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(String hyponymPath, String synsetPath) {
        super(hyponymPath, synsetPath, 3);
    }
}
