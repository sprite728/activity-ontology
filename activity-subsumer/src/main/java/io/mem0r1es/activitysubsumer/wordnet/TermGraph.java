package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

public class TermGraph implements DirectedGraph<WordNetNode, DefaultEdge> {
	private static long ID = 0;

	private long id;
	private DirectedAcyclicGraph<WordNetNode, DefaultEdge> graph;
	private WordNetNode root = null;

	public TermGraph(WordNetNode root) {
		this.id = ID++;
		this.root = root;
		graph = new DirectedAcyclicGraph<WordNetNode, DefaultEdge>(DefaultEdge.class);
		graph.addVertex(root);
	}

	public long getID() {
		return id;
	}

	@Override
	public Set<DefaultEdge> getAllEdges(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public DefaultEdge getEdge(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<WordNetNode, DefaultEdge> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public DefaultEdge addEdge(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean addEdge(WordNetNode sourceVertex, WordNetNode targetVertex, DefaultEdge e) {
		return graph.addEdge(sourceVertex, targetVertex, e);
	}

	@Override
	public boolean addVertex(WordNetNode v) {
		return graph.addVertex(v);
	}

	@Override
	public boolean containsEdge(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(DefaultEdge e) {
		return graph.containsEdge(e);
	}

	@Override
	public boolean containsVertex(WordNetNode v) {
		return graph.containsVertex(v);
	}

	@Override
	public Set<DefaultEdge> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<DefaultEdge> edgesOf(WordNetNode vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends DefaultEdge> edges) {
		return graph.removeAllEdges(edges);
	}

	@Override
	public Set<DefaultEdge> removeAllEdges(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends WordNetNode> vertices) {
		return graph.removeAllVertices(vertices);
	}

	@Override
	public DefaultEdge removeEdge(WordNetNode sourceVertex, WordNetNode targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeEdge(DefaultEdge e) {
		return graph.removeEdge(e);
	}

	@Override
	public boolean removeVertex(WordNetNode v) {
		return graph.removeVertex(v);
	}

	@Override
	public Set<WordNetNode> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public WordNetNode getEdgeSource(DefaultEdge e) {
		return graph.getEdgeSource(e);
	}

	@Override
	public WordNetNode getEdgeTarget(DefaultEdge e) {
		return graph.getEdgeTarget(e);
	}

	@Override
	public double getEdgeWeight(DefaultEdge e) {
		return graph.getEdgeWeight(e);
	}

	@Override
	public int inDegreeOf(WordNetNode vertex) {
		return graph.inDegreeOf(vertex);
	}

	@Override
	public Set<DefaultEdge> incomingEdgesOf(WordNetNode vertex) {
		return graph.incomingEdgesOf(vertex);
	}

	@Override
	public int outDegreeOf(WordNetNode vertex) {
		return graph.outDegreeOf(vertex);
	}

	@Override
	public Set<DefaultEdge> outgoingEdgesOf(WordNetNode vertex) {
		return graph.outgoingEdgesOf(vertex);
	}

	public Set<String> getWords() {
		Set<String> result = new HashSet<String>();
		for (WordNetNode node : graph.vertexSet()) {
			result.addAll(node.getWords());
		}
		return result;
	}

	public Set<WordNetNode> getNodes(String word) {
		Set<WordNetNode> result = new HashSet<WordNetNode>();
		for (WordNetNode node : graph.vertexSet()) {
			if (node.hasWord(word)) {
				result.add(node);
			}
		}
		return result;
	}

	public WordNetNode getRoot() {
		return root;
	}

	public boolean containsNonSenseTerm(String nonSenseWord) {
		for (WordNetNode node : graph.vertexSet()) {
			for (String word : node.getWords()) {
				if (Utils.wordName(word).equals(nonSenseWord)) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<WordNetNode> getSensesForNonSenseTerm(String nonSenseWord) {
		Set<WordNetNode> result = new HashSet<WordNetNode>();

		for (WordNetNode node : graph.vertexSet()) {
			for (String word : node.getWords()) {
				if (Utils.wordName(word).equals(nonSenseWord)) {
					result.add(node);
				}
			}
		}
		return result;
	}

}