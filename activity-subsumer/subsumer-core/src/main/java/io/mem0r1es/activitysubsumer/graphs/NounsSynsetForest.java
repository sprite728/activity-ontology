package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.io.SynsetProvider;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(SynsetProvider provider) {
        super(provider, 3);
    }
}
