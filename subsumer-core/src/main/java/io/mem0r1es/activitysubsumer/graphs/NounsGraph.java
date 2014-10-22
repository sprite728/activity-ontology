package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.synsets.SynsetNode;
import io.mem0r1es.activitysubsumer.synsets.SynsetStore;

/**
 * @author Ivan Gavrilović
 */
public class NounsGraph extends SynsetGraph{
    public NounsGraph(SynsetNode root) {
        super(root);
    }

    @Override
    protected SynsetStore getStore(){
        return SynsetStore.NOUNS;
    }
}
