package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.io.SynsetProvider;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.Set;

/**
 * Verbs forest - containing all of the verbs sub-graphs
 *
 * @author Ivan Gavrilović
 */
public class VerbsSynsetForest extends SynsetForest {
    public VerbsSynsetForest(SynsetProvider provider) {
        super(provider, 0);
    }

    protected void init(SynsetProvider provider, int rootLevel) {
        Set<SynsetNode> roots = nodesAtLevel(provider.rootSynsets(), rootLevel);
        logger.info("Number of sub-graphs created: " + roots.size());

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            graphs.put(node, new VerbsGraph(node));
        }
    }
}
