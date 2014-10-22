package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.synsets.Synsets;
import io.mem0r1es.activitysubsumer.synsets.SynsetNode;

import java.util.Set;

/**
 * Verbs forest - containing all of the verbs sub-graphs
 *
 * @author Ivan Gavrilović
 */
public class VerbsSynsetForest extends SynsetForest {
    public VerbsSynsetForest(Synsets synsets) {
            super(synsets, 0);
    }

    protected void init(Synsets synsets, int rootLevel) {
        Set<SynsetNode> roots = nodesAtLevel(synsets.rootSynsets(), rootLevel);

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            graphs.put(node, new VerbsGraph(node));
        }
    }
}
