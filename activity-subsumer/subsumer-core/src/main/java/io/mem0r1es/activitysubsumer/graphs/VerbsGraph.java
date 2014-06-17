package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.synsets.SynsetNode;
import io.mem0r1es.activitysubsumer.synsets.SynsetStore;

/**
 * @author Ivan Gavrilović
 */
public class VerbsGraph extends SynsetGraph{
    public VerbsGraph(SynsetNode root) {
        super(root);
    }

    @Override
    protected SynsetStore getStore() {
        return SynsetStore.VERBS;
    }
}
