package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Representing the nouns synset graph
 * @author Ivan Gavrilović
 */
public class NounsSynsetGraph extends SynsetGraph {
    public NounsSynsetGraph(String hyponymPath, String synsetPath) {
        super(null, null);
        SynsetGraphBuilder builder = new SynsetGraphBuilder(hyponymPath, synsetPath);
        DirectedAcyclicGraph<SynsetNode, DefaultEdge> nounGraph = builder.getGraph();

        for(SynsetNode sn: nounGraph.vertexSet()){
            if (nounGraph.inDegreeOf(sn) == 0){
                this.root = sn;
                break;
            }
        }
        this.graph = nounGraph;
    }
}
