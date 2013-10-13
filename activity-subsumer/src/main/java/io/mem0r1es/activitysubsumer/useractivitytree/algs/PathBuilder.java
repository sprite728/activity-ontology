package io.mem0r1es.activitysubsumer.useractivitytree.algs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;

/**
 * Class which encompasses the algorithm for finding all the paths from a given start vertex to a
 * given set of destination vertices.
 * 
 * @author horiaradu
 */
public class PathBuilder<V, E> {
	private Map<V, Set<List<V>>> pathsToDestinations = new HashMap<V, Set<List<V>>>();

	private Set<V> destinations = new HashSet<V>();
	
	private Set<V> currentDestinations = new HashSet<V>();
	private List<V> currentPath = new LinkedList<V>();
	private V startVertex;
	private Graph<V, E> graph;

	/**
	 * Constructs a new {@link PathBuilder} and computes all the paths from the startVertex to each
	 * destination in the set of destinationVertices.
	 * 
	 * @param graph
	 * @param startVertex
	 * @param destinationVertices
	 */
	public PathBuilder(Graph<V, E> graph, V startVertex, Set<V> destinationVertices) {
		this.graph = graph;
		currentDestinations.addAll(destinationVertices);
		this.startVertex = startVertex;

		dfs(startVertex);
		destinations.addAll(destinationVertices);
	}

	/**
	 * Computes all the paths from the startVertex to each destination in the set of
	 * destinationVertices.
	 * 
	 * @param startVertex
	 * @param destinationVertices
	 */
	public void computePaths(Set<V> destinations) {
		currentDestinations.clear();
		
		currentDestinations.addAll(destinations);
		currentDestinations.removeAll(this.destinations);
		
		dfs(startVertex);
		this.destinations.addAll(currentDestinations);
	}

	/**
	 * performs a Depth First Search, storing all the paths that reach one of the destinations.
	 * 
	 * @param vertex
	 */
	private void dfs(V vertex) {
		currentPath.add(vertex);
		if (currentDestinations.contains(vertex)) {
			Set<List<V>> paths = pathsToDestinations.get(vertex);
			if (paths == null) {
				paths = new HashSet<List<V>>();
				pathsToDestinations.put(vertex, paths);
			}
			paths.add(new LinkedList<V>(currentPath));
		}

		for (E edge : graph.edgesOf(vertex)) {
			V target = graph.getEdgeTarget(edge);
			V source = graph.getEdgeSource(edge);
			if (source.equals(vertex)) {
				// only look at forward edges
				dfs(target);
			}
		}

		currentPath.remove(currentPath.size() - 1);
	}

	/**
	 * @param destination
	 * @return the set of paths from the given start vertex to the destination.
	 */
	public Set<List<V>> getPathsToDestination(V destination) {
		return pathsToDestinations.get(destination);
	}

	/**
	 * @return a map containing as key, all the destinations and as values, for each vertex, the set
	 *         of paths from the given start vertex to that destination.
	 */
	public Map<V, Set<List<V>>> getAllPaths() {
		return pathsToDestinations;
	}
}
