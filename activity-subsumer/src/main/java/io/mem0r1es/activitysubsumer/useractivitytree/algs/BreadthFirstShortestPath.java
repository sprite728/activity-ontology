package io.mem0r1es.activitysubsumer.useractivitytree.algs;

import org.jgrapht.Graph;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * <p>
 * Use this to compute shortest paths on graphs where the cost function is always constant, as it
 * guarantees optimality, and the best running-time guarantee.
 * </p>
 * <p/>
 * <p>
 * Time complexity: <i>O(|V| + |E|)</i>
 * </p>
 * 
 * @author Sebastian Claici
 */
public class BreadthFirstShortestPath<V, E> {
	private final Map<V, Integer> distance;

	public BreadthFirstShortestPath(Graph<V, E> g, V src) {
		this.distance = new HashMap<V, Integer>();

		computeSingleSourceShortestPaths(g, src);
	}

	public int getCost(V dst) {
		if (!distance.containsKey(dst))
			return Integer.MAX_VALUE;
		return distance.get(dst);
	}

	private void computeSingleSourceShortestPaths(Graph<V, E> g, V src) {
		Queue<V> queue = new ArrayDeque<V>();
		Set<V> visited = new HashSet<V>();

		queue.add(src);
		visited.add(src);
		distance.put(src, 0);
		while (!queue.isEmpty()) {
			V node = queue.poll();

			int d = distance.get(node);
			for (E edge : g.edgesOf(node)) {
				V next = g.getEdgeTarget(edge);
				if (!visited.contains(next)) {
					queue.add(next);
					visited.add(next);
					distance.put(next, d + 1);
				}
			}
		}
	}
}
