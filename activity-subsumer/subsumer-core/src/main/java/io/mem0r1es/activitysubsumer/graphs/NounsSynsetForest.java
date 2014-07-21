package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.synsets.Synsets;
import io.mem0r1es.activitysubsumer.synsets.SynsetNode;

import java.util.Set;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(Synsets synsets) {
        super(synsets, 3);
    }

    protected void init(Synsets synsets, int rootLevel) {
        Set<SynsetNode> roots = nodesAtLevel(synsets.rootSynsets(), rootLevel);

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            graphs.put(node, new NounsGraph(node));
        }
    }
}
