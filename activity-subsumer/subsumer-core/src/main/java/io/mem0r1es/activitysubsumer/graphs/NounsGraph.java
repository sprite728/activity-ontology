package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.Dict;
import io.mem0r1es.activitysubsumer.wordnet.NounDict;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

/**
 * @author Ivan Gavrilović
 */
public class NounsGraph extends SynsetGraph{
    public NounsGraph(SynsetNode root) {
        super(root);
    }

    @Override
    protected Dict getDictionary() {
        return NounDict.getInstance();
    }
}
