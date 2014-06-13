package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.io.SynsetProvider;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.Set;

/**
 * Noun forest - contains all of the noun sub-graphs
 * @author Ivan Gavrilović
 */
public class NounsSynsetForest extends SynsetForest {
    public NounsSynsetForest(SynsetProvider provider) {
        super(provider, 3);
    }

    protected void init(SynsetProvider provider, int rootLevel) {
        Set<SynsetNode> roots = nodesAtLevel(provider.rootSynsets(), rootLevel);
        logger.info("Number of sub-graphs created: " + roots.size());

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            graphs.put(node, new NounsGraph(node));
        }
    }
}
