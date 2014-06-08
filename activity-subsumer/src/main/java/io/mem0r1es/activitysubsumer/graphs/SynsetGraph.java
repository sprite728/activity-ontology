package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.utils.SubsumerLogger;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.apache.log4j.Logger;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;

/**
 * Direct acyclic graph containing the {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} as nodes,
 * and with specified root
 *
 * @author Ivan Gavrilović
 */
public class SynsetGraph {
    static Logger logger = SubsumerLogger.getLogger(SynsetGraph.class);
    protected SynsetNode root;
    protected DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph;

    public SynsetGraph(SynsetNode root, DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph) {
        this.root = root;
        this.graph = graph;
    }

    /**
     * Find word in the whole graph
     *
     * @param word search term
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} containing the word
     */
    public Set<SynsetNode> find(String word) {
        return find(word, root);
    }

    /**
     * Find the word in the sub-graph specified by {@code startNode}
     *
     * @param word      search term
     * @param startNode starting node for search
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} containing the word
     */
    public Set<SynsetNode> find(String word, SynsetNode startNode) {
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();

        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graph, startNode);
        while (bfs.hasNext()) {
            SynsetNode node = bfs.next();
            if (node.contains(word)) resultSet.add(node);
        }
        return resultSet;
    }

    /**
     * Get all nodes that have startNode as their parent
     *
     * @param startNode starting node
     * @return set of synset nodes
     */
    public Set<SynsetNode> getAllFrom(SynsetNode startNode) {
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();

        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graph, startNode);
        while (bfs.hasNext()) {
            resultSet.add(bfs.next());
        }
        return resultSet;
    }

    /**
     * Find the words in the sub-graph
     *
     * @param words search term
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} containing the word
     */
    public Set<SynsetNode> findAll(Set<String> words) {
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();
        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graph, root);
        while (bfs.hasNext()) {
            SynsetNode node = bfs.next();
            if (node.containsAny(words)) resultSet.add(node);
        }
        return resultSet;
    }

    /**
     * Check if this sub-graph contains the specified word
     *
     * @param word serach term
     * @return {@code true} if sub-graph contains it, {@code false} otherwise
     */
    public boolean contains(String word) {
        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graph, root);
        while (bfs.hasNext()) {
            SynsetNode node = bfs.next();
            if (node.contains(word)) return true;
        }
        return false;
    }

    /**
     * Find least common ancestor for the set of nodes
     *
     * @param nodes set of nodes to find the LCAs for
     * @return set of LCAs
     */
    public Set<SynsetNode> getLCA(Set<SynsetNode> nodes) {
        logger.debug("Finding LCA for: " + nodes);
        List<SynsetNode> listNodes = new ArrayList<SynsetNode>(nodes);
        Set<SynsetNode> previousLCAs = new HashSet<SynsetNode>();
        previousLCAs.add(listNodes.get(0));

        /**
         * Find the LCAs for 1st and 2nd node. Let that be [x,y,z]
         * Then find LCAs for 3rd and x, 3rd and y, 3rd and z etc.
         */
        for (int i = 1; i < listNodes.size(); i++) {
            Set<SynsetNode> currentSet = new HashSet<SynsetNode>(previousLCAs);
            currentSet.add(listNodes.get(i - 1));

            previousLCAs.clear();
            for (SynsetNode s : currentSet) {
                previousLCAs.addAll(getLCA(s, listNodes.get(i)));
            }
        }

        return previousLCAs;
    }

    /**
     * Finds set of leas common ancestors for the two nodes. It finds the paths from each of the nodes to the root. For
     * each pair of the paths if find the difference in the length, and we forward the node that is further from the root
     * for number of hoops it is further. At one point the paths will intersect.
     *
     * @param fst first node
     * @param snd second node
     * @return set of LCAs
     */
    public Set<SynsetNode> getLCA(SynsetNode fst, SynsetNode snd) {
        List<List<SynsetNode>> fstPaths = getAllPathsToRoot(fst);
        List<List<SynsetNode>> sndPaths = getAllPathsToRoot(snd);

        Set<SynsetNode> lcas = new HashSet<SynsetNode>();
        for (List<SynsetNode> path1 : fstPaths) {
            for (List<SynsetNode> path2 : sndPaths) {
                int length1 = path1.size();
                int length2 = path2.size();
                int diff = length1 - length2;

                List<SynsetNode> longer = diff > 0 ? path1 : path2;
                List<SynsetNode> shorter = diff > 0 ? path2 : path1;
                diff = Math.abs(diff);

                for (int i = 0; i < shorter.size(); i++) {
                    if (shorter.get(i) == longer.get(diff + i)) {
                        lcas.add(shorter.get(i));
                        break;
                    }
                }
            }
        }

        return lcas;
    }

    /**
     * Find all paths from the node to the root
     *
     * @param startNode starting node
     * @return list containing the paths from startNode to root
     */
    public List<List<SynsetNode>> getAllPathsToRoot(SynsetNode startNode) {
        List<List<SynsetNode>> paths = new LinkedList<List<SynsetNode>>();
        if (startNode == root) {
            List<SynsetNode> myPath = new LinkedList<SynsetNode>();
            myPath.add(startNode);
            paths.add(myPath);
            return paths;

        }

        for (DefaultEdge e : graph.incomingEdgesOf(startNode)) {
            SynsetNode next = graph.getEdgeSource(e);
            for (List<SynsetNode> c : getAllPathsToRoot(next)) {
                List<SynsetNode> myPath = new LinkedList<SynsetNode>(c);
                myPath.add(0, startNode);
                paths.add(myPath);
            }
        }

        return paths;
    }


    public SynsetNode getRoot() {
        return root;
    }

    public DirectedAcyclicGraph<SynsetNode, DefaultEdge> getGraph() {
        return graph;
    }
}
