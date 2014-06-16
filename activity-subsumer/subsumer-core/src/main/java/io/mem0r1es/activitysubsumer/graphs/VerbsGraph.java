package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.Dict;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.SynsetStore;

/**
 * @author Ivan Gavrilović
 */
public class VerbsGraph extends SynsetGraph{
    public VerbsGraph(SynsetNode root) {
        super(root);
    }

    @Override
    protected Dict getDictionary() {
        return Dict.VERBS;
    }

    @Override
    protected SynsetStore getStore() {
        return SynsetStore.VERBS;
    }
}
