package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetPool;

/**
 * Verbs forest - containing all of the verbs sub-graphs
 *
 * @author Ivan Gavrilović
 */
public class VerbsSynsetForest extends SynsetForest {
    public VerbsSynsetForest(SynsetPool pool) {
        super(pool, 0);
    }
}
