package io.mem0r1es.activitysubsumer.algs;

import io.mem0r1es.activitysubsumer.utils.Pair;
import org.jgrapht.Graph;

import java.util.*;

/**
 * Class which encompasses the algorithm for finding all the paths from a given start vertex to a
 * given set of destination vertices.
 *
 * @author Horia Radu
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
     *            the graph
     * @param startVertex
     *            the start vertex
     * @param destinationVertices
     *            the set of destination vertices
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
     * @param destinations
     *            the set of destination vertices
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
     *            the current vertex
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
     *            the destination
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

    private Pair<Set<V>, Integer> bestCommonAncestor = null;

    /**
     * The Lowest common ancestor of the given set of vertices
     *
     * @param destinations
     *            the set of vertices
     * @return the set of equally good lowest common ancestors
     */
    public Set<V> getLCA(Set<V> destinations) {
        // All the paths are in the same sub-graph => we care about the DEEPEST common ancestor. We
        // need to find all the possible combinations of paths that reach the destinations and see
        // who is the LCA. Then select the deepest LCA.
        bestCommonAncestor = null;

        List<V> nodes = new ArrayList<V>();
        for (V destination : destinations) {
            nodes.add(destination);
        }

        recursiveCrap(nodes, 0, new HashSet<List<V>>());

        if (bestCommonAncestor != null) {
            return bestCommonAncestor.first;
        }
        return null;
    }

    private void recursiveCrap(List<V> nodes, int depth, Set<List<V>> solution) {
        V current = nodes.get(depth);
        Set<List<V>> paths = pathsToDestinations.get(current);
        if (paths == null) {
            return;
        }
        for (List<V> path : paths) {
            solution.add(path);
            if (depth == nodes.size() - 1) {
                findLCA(solution);
            } else {
                recursiveCrap(nodes, depth + 1, solution);
            }
            solution.remove(path);
        }
    }

    private void findLCA(Set<List<V>> solution) {
        boolean over = false;
        int i = 0;
        while(!over){
            V currentNode = null;

            for (List<V> path : solution) {
                if (i == path.size()) {
                    over = true;
                    break;
                } else {
                    if (currentNode == null) {
                        currentNode = path.get(i);
                    } else if (!path.get(i).equals(currentNode)) {
                        over = true;
                        break;
                    }
                }
            }
            i++;
        }

        List<V> firstPath = solution.iterator().next();
        if (0 <= i - 2) {
            V lca = firstPath.get(i - 2);
            if (bestCommonAncestor == null || bestCommonAncestor.second < i - 2) {
                bestCommonAncestor = new Pair<Set<V>, Integer>(new HashSet<V>(), i - 2);
                bestCommonAncestor.first.add(lca);
            } else if (bestCommonAncestor.second == i - 2) {
                bestCommonAncestor.first.add(lca);
            }
        }
    }
}