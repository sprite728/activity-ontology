package io.mem0r1es.activitysubsumer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

/**
 * Various utility methods that work on graphs.
 * 
 * @author Sebastian Claici
 */
public final class GraphUtils {
	public static final String DEFAULT_ROOT = "DEFAULT_ROOT";

	private GraphUtils() {
	}

	/**
	 * Reads a directed acyclic graph (DAG) from a file
	 * 
	 * @param filename
	 *            file name (or path) from which to read
	 * @param edgeClazz
	 *            class of the edges to use (JGraphT needs this)
	 */
	public static <E> DirectedAcyclicGraph<String, E> buildDAG(String filename, Class<? extends E> edgeClazz) throws IOException {
		File file = new File(filename);

		return buildDAG(file, edgeClazz);
	}

	/**
	 * Reads a directed acyclic graph (DAG) from a file
	 * 
	 * @param file
	 *            {@link java.io.File} object from which to read
	 * @param edgeClazz
	 *            class of the edges to use (JGraphT needs this)
	 */
	public static <E> DirectedAcyclicGraph<String, E> buildDAG(File file, Class<? extends E> edgeClazz) throws IOException {
		InputStream is = new FileInputStream(file);

		return buildDAG(is, edgeClazz);
	}

	/**
	 * Reads a directed acyclic graph (DAG) from an <b>InputStream</b>
	 * 
	 * @param is
	 *            {@link java.io.InputStream} object from which to read
	 * @param edgeClazz
	 *            class of the edges to use (JGraphT needs this)
	 */
	public static <E> DirectedAcyclicGraph<String, E> buildDAG(InputStream is, Class<? extends E> edgeClazz) throws IOException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));

		DirectedAcyclicGraph<String, E> dag = new DirectedAcyclicGraph<String, E>(edgeClazz);
		for (String line = buf.readLine(); line != null; line = buf.readLine()) {
			if (line.startsWith("#"))
				continue;

			StringTokenizer tokenizer = new StringTokenizer(line);
			String node = tokenizer.nextToken();
			dag.addVertex(node);

			while (tokenizer.hasMoreTokens()) {
				String next = tokenizer.nextToken();
				dag.addVertex(next);
				dag.addEdge(node, next);
			}
		}

		dag.addVertex(DEFAULT_ROOT);
		for (String vertex : dag.vertexSet()) {
			if (dag.inDegreeOf(vertex) == 0 && vertex.equals(DEFAULT_ROOT) == false) {
				dag.addEdge(DEFAULT_ROOT, vertex);
			}
		}

		return dag;
	}

	/**
	 * @return a {@link java.util.Set} of ancestors of a node in a {@link org.jgrapht.DirectedGraph}
	 */
	public static <V, E> Set<V> ancestors(DirectedGraph<V, E> graph, V node) {
		DirectedGraph<V, E> reversedGraph = reverseGraph(graph);
		BreadthFirstIterator<V, E> iter = new BreadthFirstIterator<V, E>(reversedGraph, node);

		iter.next();
		Set<V> result = new LinkedHashSet<V>();
		while (iter.hasNext())
			result.add(iter.next());

		return result;
	}

	/**
	 * @return a {@link java.util.Set} of descendants of a node in a
	 *         {@link org.jgrapht.DirectedGraph}
	 */
	public static <V, E> Set<V> descendants(DirectedGraph<V, E> graph, V node) {
		BreadthFirstIterator<V, E> iter = new BreadthFirstIterator<V, E>(graph, node);

		iter.next();
		Set<V> result = new LinkedHashSet<V>();
		while (iter.hasNext())
			result.add(iter.next());

		return result;
	}

	public static <V, E> V parent(DirectedGraph<V, E> tree, V node) {
		DirectedGraph<V, E> reversedTree = reverseGraph(tree);
		BreadthFirstIterator<V, E> iter = new BreadthFirstIterator<V, E>(reversedTree, node);

		iter.next();
		if (iter.hasNext())
			return iter.next();
		else
			return node;
	}

	/**
	 * Reverse a {@link org.jgrapht.DirectedGraph} in place
	 */
	public static <V, E> void reverseGraphInPlace(DirectedGraph<V, E> graph) {
		Set<E> edges = graph.edgeSet();
		graph.removeAllEdges(edges);

		for (E edge : edges) {
			V src = graph.getEdgeSource(edge);
			V dst = graph.getEdgeTarget(edge);
			graph.addEdge(dst, src);
		}
	}

	/**
	 * @return reversed copy of a {@link org.jgrapht.DirectedGraph}
	 */
	public static <V, E> DirectedGraph<V, E> reverseGraph(DirectedGraph<V, E> graph) {
		return new EdgeReversedGraph<V, E>(graph);
	}

	/**
	 * <p>
	 * Find the deepest node in a directed acyclic graph from a set of nodes.
	 * </p>
	 * <p>
	 * The depth of a node is the maximum depth through any path that reaches that node.
	 * </p>
	 * 
	 * @return the single deepest node under the conditions outlined above
	 */
	public static <V, E> V findDeepest(Set<V> nodes, DirectedAcyclicGraph<V, E> graph) {
		Map<V, Integer> depthMap = bfsExplore(graph);

		V bestNode = null;
		int best = Integer.MIN_VALUE;
		for (V node : nodes) {
			if (depthMap.containsKey(node) && depthMap.get(node) > best) {
				best = depthMap.get(node);
				bestNode = node;
			}
		}

		return bestNode;
	}

	/**
	 * <p>
	 * Return the maximum depth of <i>node</i> in <i>graph</i>
	 * </p>
	 * 
	 * @return an integer representing the maximum depth at which <i>node</i> can be found
	 */
	public static <V, E> int findMaximumDepth(V node, DirectedAcyclicGraph<V, E> graph) {
		Map<V, Integer> depthMap = bfsExplore(graph);
		return depthMap.get(node);
	}

	/**
	 * <p>
	 * Explore a directed acyclic graph using a breadth first search approach and create a depth map
	 * for the nodes.
	 * </p>
	 * <p>
	 * The depth of a node is the maximum depth through any path that reaches that node.
	 * </p>
	 * 
	 * @return a map between nodes and the maximum depth at which it can be found
	 */
	public static <V, E> Map<V, Integer> bfsExplore(DirectedAcyclicGraph<V, E> graph) {
		Deque<V> queue = new ArrayDeque<V>();
		Map<V, Integer> depthMap = new HashMap<V, Integer>();
		Set<V> visited = new HashSet<V>();

		for (V vertex : graph.vertexSet()) {
			if (graph.inDegreeOf(vertex) == 0) {
				queue.add(vertex);
				depthMap.put(vertex, 0);
			}
		}

		while (!queue.isEmpty()) {
			V vertex = queue.poll();
			int dist = depthMap.get(vertex);

			for (E edge : graph.outgoingEdgesOf(vertex)) {
				V next = graph.getEdgeTarget(edge);
				if (!visited.contains(next) || (depthMap.containsKey(next) && (dist + 1) > depthMap.get(next))) {
					depthMap.put(next, dist + 1);
					visited.add(next);
					queue.add(next);
				}
			}
		}

		return depthMap;
	}
}
