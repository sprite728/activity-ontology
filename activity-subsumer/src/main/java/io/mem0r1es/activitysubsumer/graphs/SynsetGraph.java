package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class SynsetGraph {
    protected SynsetNode root;
    protected DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph;

    public SynsetGraph(SynsetNode root, DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph) {
        this.root = root;
        this.graph = graph;
    }

    /**
     * Find word in the whole graph
     * @param word search term
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} containing the word
     */
    public Set<SynsetNode> find(String word){
       return find(word, root);
    }

    /**
     * Find the word in the sub-graph specified by {@code startNode}
     * @param word search term
     * @param startNode starting node for search
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} containing the word
     */
    public Set<SynsetNode> find(String word, SynsetNode startNode){
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();

        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graph, startNode);
        while (bfs.hasNext()){
            SynsetNode node = bfs.next();
            if (node.contains(word)) resultSet.add(node);
        }
        return resultSet;
    }

    public SynsetNode getRoot() {
        return root;
    }

    public DirectedAcyclicGraph<SynsetNode, DefaultEdge> getGraph() {
        return graph;
    }
}
