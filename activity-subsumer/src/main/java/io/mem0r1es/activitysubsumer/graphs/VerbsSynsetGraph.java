package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Ivan Gavrilović
 */
public class VerbsSynsetGraph extends SynsetGraph {
    public VerbsSynsetGraph(SynsetNode root, DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph) {
        super(root, graph);
    }
}
