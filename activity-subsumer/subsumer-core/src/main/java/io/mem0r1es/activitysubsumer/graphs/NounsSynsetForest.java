package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetPool;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(SynsetPool pool) {
        super(pool, 3);
    }
}
