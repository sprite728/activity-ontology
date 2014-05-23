package io.mem0r1es.activitysubsumer.wordnet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Class which represents a WordNet sub-graph.
 *
 * @author Horia Radu
 */
public class TermGraph implements DirectedGraph<SynsetNode, DefaultEdge> {
    private static long ID = 0;

    private long id;
    private DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph;
    private SynsetNode root = null;

    public TermGraph(SynsetNode root) {
        this.id = ID++;
        this.root = root;
        graph = new DirectedAcyclicGraph<SynsetNode, DefaultEdge>(DefaultEdge.class);
        graph.addVertex(root);
    }

    // TEMP CODE: helps for testing
    public TermGraph(SynsetNode root, long id) {
        this.id = id;
        this.root = root;
        graph = new DirectedAcyclicGraph<SynsetNode, DefaultEdge>(DefaultEdge.class);
        graph.addVertex(root);
    }

    public long getID() {
        return id;
    }

    /**
     * @return - the set of words in the sub-graph.
     */
    public Set<String> getWords() {
        Set<String> result = new HashSet<String>();
        for (SynsetNode node : graph.vertexSet()) {
            result.addAll(node.getSynset());
        }
        return result;
    }

    /**
     * @param word
     *            the given word (without sense)
     * @return - the set of nodes which contain that word.
     */
    public Set<SynsetNode> getNodes(String word) {
        return getNodes(Collections.singleton(word));
    }

    public Set<SynsetNode> getNodes(Set<String> words) {
        Set<SynsetNode> result = new HashSet<SynsetNode>();
        for (SynsetNode node : graph.vertexSet()) {
            for (String word : node.getSynset()) {
                if (words.contains(word)) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * @param word
     *            the given word (without sense)
     * @return the set of nodes which contain that word.
     */
    public Set<SynsetNode> getNodesForNonSenseTerm(String word) {
        return getNodesForNonSenseTerms(Collections.singleton(word));
    }

    /**
     * @param words
     *            the give words (without senses)
     * @return the set of nodes which contain that word.
     */
    public Set<SynsetNode> getNodesForNonSenseTerms(Set<String> words) {
        Set<SynsetNode> result = new HashSet<SynsetNode>();
        for (SynsetNode node : graph.vertexSet()) {
            for (String word : node.getSynset()) {
                if (words.contains(WordNetUtils.wordName(word))) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * @return the root of the sub-graph.
     */
    public SynsetNode getRoot() {
        return root;
    }

    /**
     * @param nonSenseWord
     *            the word (without sense)
     * @return true if the sub-graph contains a sense of the given word (without a sense), false
     *         otherwise
     */
    public boolean containsNonSenseTerm(String nonSenseWord) {
        for (SynsetNode node : graph.vertexSet()) {
            for (String word : node.getSynset()) {
                if (WordNetUtils.wordName(word).equals(nonSenseWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param nonSenseWord
     *            the word (without sense)
     * @return the set of nodes which contain a sense of the given word (without a sense)
     */
    public Set<SynsetNode> getSensesForNonSenseTerm(String nonSenseWord) {
        return getSensesForNonSenseTerms(Collections.singleton(nonSenseWord));
    }

    /**
     * @param nonSenseWords
     *            the words (without sense)
     * @return the set of nodes which contain a sense of the given word (without a sense)
     */
    public Set<SynsetNode> getSensesForNonSenseTerms(Set<String> nonSenseWords) {
        Set<SynsetNode> result = new HashSet<SynsetNode>();

        for (SynsetNode node : graph.vertexSet()) {
            for (String word : node.getSynset()) {
                if (nonSenseWords.contains(WordNetUtils.wordName(word))) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    @Override
    public Set<DefaultEdge> getAllEdges(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.getAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public DefaultEdge getEdge(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.getEdge(sourceVertex, targetVertex);
    }

    @Override
    public EdgeFactory<SynsetNode, DefaultEdge> getEdgeFactory() {
        return graph.getEdgeFactory();
    }

    @Override
    public DefaultEdge addEdge(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(SynsetNode sourceVertex, SynsetNode targetVertex, DefaultEdge e) {
        return graph.addEdge(sourceVertex, targetVertex, e);
    }

    @Override
    public boolean addVertex(SynsetNode v) {
        return graph.addVertex(v);
    }

    @Override
    public boolean containsEdge(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.containsEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean containsEdge(DefaultEdge e) {
        return graph.containsEdge(e);
    }

    @Override
    public boolean containsVertex(SynsetNode v) {
        return graph.containsVertex(v);
    }

    @Override
    public Set<DefaultEdge> edgeSet() {
        return graph.edgeSet();
    }

    @Override
    public Set<DefaultEdge> edgesOf(SynsetNode vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends DefaultEdge> edges) {
        return graph.removeAllEdges(edges);
    }

    @Override
    public Set<DefaultEdge> removeAllEdges(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.removeAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends SynsetNode> vertices) {
        return graph.removeAllVertices(vertices);
    }

    @Override
    public DefaultEdge removeEdge(SynsetNode sourceVertex, SynsetNode targetVertex) {
        return graph.removeEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeEdge(DefaultEdge e) {
        return graph.removeEdge(e);
    }

    @Override
    public boolean removeVertex(SynsetNode v) {
        return graph.removeVertex(v);
    }

    @Override
    public Set<SynsetNode> vertexSet() {
        return graph.vertexSet();
    }

    @Override
    public SynsetNode getEdgeSource(DefaultEdge e) {
        return graph.getEdgeSource(e);
    }

    @Override
    public SynsetNode getEdgeTarget(DefaultEdge e) {
        return graph.getEdgeTarget(e);
    }

    @Override
    public double getEdgeWeight(DefaultEdge e) {
        return graph.getEdgeWeight(e);
    }

    @Override
    public int inDegreeOf(SynsetNode vertex) {
        return graph.inDegreeOf(vertex);
    }

    @Override
    public Set<DefaultEdge> incomingEdgesOf(SynsetNode vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(SynsetNode vertex) {
        return graph.outDegreeOf(vertex);
    }

    @Override
    public Set<DefaultEdge> outgoingEdgesOf(SynsetNode vertex) {
        return graph.outgoingEdgesOf(vertex);
    }
}