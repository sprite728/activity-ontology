package io.mem0r1es.activitysubsumer.useractivitytree.algs;

import io.mem0r1es.activitysubsumer.useractivitytree.utils.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author horiaradu
 */
public class DistanceMeasurer<V, E> {

	private PathBuilder<V, E> pathBuilder;
	private Map<Pair<V, V>, Pair<Double, Set<Pair<List<V>, List<V>>>>> paths = new HashMap<Pair<V, V>, Pair<Double, Set<Pair<List<V>, List<V>>>>>();

	public DistanceMeasurer(PathBuilder<V, E> pathBuilder) {
		this.pathBuilder = pathBuilder;
	}

	public Pair<Double, Set<Pair<List<V>, List<V>>>> getBestPaths(V vertex1, V vertex2) {
		Pair<V, V> key = new Pair<V, V>(vertex1, vertex2);
		Pair<Double, Set<Pair<List<V>, List<V>>>> result = paths.get(key);
		if (result != null) {
			return result;
		}

		result = new Pair<Double, Set<Pair<List<V>, List<V>>>>(null, new HashSet<Pair<List<V>, List<V>>>());
		paths.put(key, result);

		double maxDistance = 0;
		for (List<V> path1 : pathBuilder.getPathsToDestination(vertex1)) {
			for (List<V> path2 : pathBuilder.getPathsToDestination(vertex2)) {
				double distance = distance(path1, path2);
				if (distance > maxDistance) {
					maxDistance = distance;
					result.first = distance;
					result.second.add(new Pair<List<V>, List<V>>(path1, path2));
				} else if (distance == maxDistance) {
					result.second.add(new Pair<List<V>, List<V>>(path1, path2));
				}
			}
		}

		return result;
	}

	private double distance(List<V> path1, List<V> path2) {
		int intersection = 0;
		for (V node : path1) {
			if (path2.contains(node)) {
				intersection++;
			}
		}

		Set<V> temp = new HashSet<V>(path1);
		temp.addAll(path2);
		return (double) intersection / temp.size();
	}
}
