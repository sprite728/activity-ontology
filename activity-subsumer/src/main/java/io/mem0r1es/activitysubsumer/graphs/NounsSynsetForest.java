package io.mem0r1es.activitysubsumer.graphs;

import java.io.InputStream;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(InputStream hyponymStream, InputStream synsetStream) {
        super(hyponymStream, synsetStream, 3, 198389, 82192, 146547, 84505);
    }
}
