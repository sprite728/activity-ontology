package io.mem0r1es.activitysubsumer.io;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Ivan Gavrilović
 */
public interface FoursquareCategoriesParser {
    DirectedAcyclicGraph<String, DefaultEdge> readGraph();
}
