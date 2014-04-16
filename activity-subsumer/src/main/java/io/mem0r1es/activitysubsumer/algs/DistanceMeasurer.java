package io.mem0r1es.activitysubsumer.algs;

import io.mem0r1es.activitysubsumer.utils.Pair;

import java.util.*;

/**
 * Class which encompasses the algorithm to measure the distance between two vertices.
 *
 * @author Horia Radu
 */
@SuppressWarnings("unused")
public class DistanceMeasurer<V, E> {

    private PathBuilder<V, E> pathBuilder;
    private Map<Pair<V, V>, Pair<Double, Set<Pair<List<V>, List<V>>>>> paths =
            new HashMap<Pair<V, V>, Pair<Double, Set<Pair<List<V>, List<V>>>>>();

    public DistanceMeasurer(PathBuilder<V, E> pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    /**
     * The method returns the best paths between two vertices, based on the paths present in the
     * {@link PathBuilder} supplied as a parameter. The distance metric is the following: the
     * distance between two vertices, x and y, is equal to the ratio between the number of common
     * nodes between the two paths (from x to the default root, respectively from y to the default
     * root) and the total number of distinct nodes in the two paths.
     *
     * @param vertex1
     *            the first vertex
     * @param vertex2
     *            the second vertex
     * @return a pair which contains the distance between the two vertices and the set of pairs of
     *         paths which have led to that score. These pairs of paths are the ones yielding the
     *         best distance.
     */
    public Pair<Double, Set<Pair<List<V>, List<V>>>> getBestPaths(V vertex1, V vertex2) {
        Pair<V, V> key = new Pair<V, V>(vertex1, vertex2);
        Pair<Double, Set<Pair<List<V>, List<V>>>> result = paths.get(key);
        if (result != null) {
            return result;
        }

        result =
                new Pair<Double, Set<Pair<List<V>, List<V>>>>(null,
                        new HashSet<Pair<List<V>, List<V>>>());
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

    /**
     * @param path1
     *            the first path
     * @param path2
     *            the second path
     * @return Ratio between the number of nodes of the intersection and the number of nodes in the
     *         union of the two paths.
     */
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