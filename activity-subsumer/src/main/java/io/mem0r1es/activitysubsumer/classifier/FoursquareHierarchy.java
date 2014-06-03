package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.io.FoursquareCategoriesParser;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class FoursquareHierarchy {
    private DirectedAcyclicGraph<String, DefaultEdge> categories = null;
    private FoursquareCategoriesParser parser;

    public FoursquareHierarchy(FoursquareCategoriesParser parser) {
        this.parser = parser;
    }

    /**
     * Get all categories that are more general than the specified one
     * @param s category for which we are looking more general ones
     * @return all parent categories, including the specified one
     */
    public Set<String> getHierarchy(String s) {
        if (categories == null) {
            categories = parser.readGraph();
        }

        Set<String> resultSet = new HashSet<String>();
        resultSet.add(s);
        Set<DefaultEdge> edges = categories.incomingEdgesOf(s);
        while (!edges.isEmpty()) {
            Set<DefaultEdge> nextEdgs = new HashSet<DefaultEdge>();
            for (DefaultEdge de : edges) {
                String source = categories.getEdgeSource(de);
                resultSet.add(source);
                nextEdgs.addAll(categories.incomingEdgesOf(source));
            }
            edges = new HashSet<DefaultEdge>(nextEdgs);
        }
        return resultSet;
    }
}
