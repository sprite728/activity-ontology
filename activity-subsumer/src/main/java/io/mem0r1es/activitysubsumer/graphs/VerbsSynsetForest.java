package io.mem0r1es.activitysubsumer.graphs;

import java.io.InputStream;

/**
 * Verbs forest - containing all of the verbs sub-graphs
 * @author Ivan Gavrilović
 */
public class VerbsSynsetForest extends SynsetForest {
    public VerbsSynsetForest(InputStream hyponymStream, InputStream synsetStream) {
        super(hyponymStream, synsetStream, 0, 25129, 13789, 25061, 13256);
    }
}
