package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

/**
 * Implement this to map the codes from the activities graph file to the activities
 *
 * @author Ivan Gavrilović
 */
interface ActivityMappingParser {
    boolean writeMappings(Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters);

    Map<String, BasicActivity> readMappings();

    BasicActivity codeToActivity(String code);
}
