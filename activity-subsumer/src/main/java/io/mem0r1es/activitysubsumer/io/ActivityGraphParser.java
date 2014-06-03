package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

/**
 * Interfaces that should be implemented by classes that persist the graph
 * @author Ivan Gavrilović
 */
interface ActivityGraphParser {
    boolean write(Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters);
    Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> readGraph();
}
