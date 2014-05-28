package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains all of the verbs graphs, which are not connected in the WordNet
 *
 * @author Ivan Gavrilović
 */
public class SynsetForest {
    private Map<SynsetNode, VerbsSynsetGraph> graphs = new HashMap<SynsetNode, VerbsSynsetGraph>();

    public SynsetForest(String hyponymPath, String synsetPath) {
        init(hyponymPath, synsetPath);
    }

    private void init(String hyponym, String synset) {
        SynsetGraphBuilder builder = new SynsetGraphBuilder(hyponym, synset);
        DirectedAcyclicGraph<SynsetNode, DefaultEdge> verbGraph = builder.getGraph();

        // get nodes with no incoming edges
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode s : verbGraph.vertexSet()) {
            if (verbGraph.inDegreeOf(s) == 0) {
                roots.add(s);
            }
        }

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            VerbsSynsetGraph vsg = new VerbsSynsetGraph(node, verbGraph);
            graphs.put(node, vsg);
        }
    }

    /**
     * Returns the roots of the sub-graphs containing the word
     * @param word search term
     * @return set with roots of the sub-graphs
     */
    public Set<SynsetNode> find(String word) {
        Set<SynsetNode> resultRoots = new HashSet<SynsetNode>();
        for (Map.Entry<SynsetNode, VerbsSynsetGraph> entry : graphs.entrySet()) {
            if (!entry.getValue().find(word).isEmpty()){
                resultRoots.add(entry.getKey());
            }
        }
        return resultRoots;
    }
}