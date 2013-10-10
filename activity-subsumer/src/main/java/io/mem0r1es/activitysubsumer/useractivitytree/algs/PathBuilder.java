package io.mem0r1es.activitysubsumer.useractivitytree.algs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;

/**
 * @author horiaradu
 */
public class PathBuilder<V, E> {
	private Map<V, Set<List<V>>> pathsToDestinations = new HashMap<V, Set<List<V>>>();
	private Set<V> destinations = new HashSet<V>();
	private List<V> currentPath = new LinkedList<V>();
	private Graph<V, E> graph;

	public PathBuilder(Graph<V, E> graph, V startVertex, Set<V> destinationVertices) {
		this.graph = graph;
		destinations.addAll(destinationVertices);

		dfs(startVertex);
	}

	private void dfs(V vertex) {
		currentPath.add(vertex);
		if (destinations.contains(vertex)) {
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

	public Set<List<V>> getPathsToDestination(V destination) {
		return pathsToDestinations.get(destination);
	}

	public Map<V, Set<List<V>>> getAllPaths() {
		return pathsToDestinations;
	}
}
