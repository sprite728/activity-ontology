package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.concurrent.ActivityOpsExecutor;
import io.mem0r1es.activitysubsumer.synsets.SynsetNode;
import io.mem0r1es.activitysubsumer.synsets.Synsets;
import io.mem0r1es.activitysubsumer.utils.BFSHierarchicalNode;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Contains all of the synset graphs, which are not connected in the WordNet
 *
 * @author Ivan Gavrilović
 */
public abstract class SynsetForest {

    protected Map<SynsetNode, SynsetGraph> graphs = new HashMap<SynsetNode, SynsetGraph>();
    protected Map<SynsetNode, Integer> subgraphSizes = new HashMap<SynsetNode, Integer>();

    /**
     * Generates a forest of synsets
     *
     * @param synsets      synset pool used to retrieve all synsets
     * @param rootLevel at which level should we split the big graph. If it is 0, only the nodes with in degree 0 are taken,
     *                  if it is 1, the first neighbours are added, if 2, neighbours of neighbours are added, and so on
     */
    public SynsetForest(Synsets synsets, int rootLevel) {
        init(synsets, rootLevel);
    }

    protected abstract void init(Synsets synsets, int rootLevel);

    /**
     * Returns the roots of the sub-graphs containing the word
     *
     * @param word search term
     * @return set with roots of the sub-graphs
     */
    public Set<SynsetNode> findSubgraphs(final String word) {
        Set<SynsetNode> resultRoots = new HashSet<SynsetNode>();
        List<Future<SynsetNode>> futures = new LinkedList<Future<SynsetNode>>();
        for (final Map.Entry<SynsetNode, SynsetGraph> entry : graphs.entrySet()) {
            Future<SynsetNode> worker = ActivityOpsExecutor.get().submit(new Callable<SynsetNode>() {
                @Override
                public SynsetNode call() throws Exception {
                    if (entry.getValue().contains(word)) {
                        return entry.getKey();
                    }
                    return null;
                }
            });
            futures.add(worker);
        }

        try {
            for (Future<SynsetNode> futSyn : futures) {
                SynsetNode res = futSyn.get();
                if (res != null) resultRoots.add(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultRoots;
    }
    static int cnt = 0;

    /**
     * Finds all synset nodes containing any of the specified words
     *
     * @param subgraphRoot root of the sub-graph to search in
     * @param words        words to find
     * @return set of {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode}
     */
    public Set<SynsetNode> findAllInSubgraph(SynsetNode subgraphRoot, Set<String> words) {
        try {
            if (subgraphRoot.getParents() == null) cnt++;
            return graphs.get(subgraphRoot).findAll(words);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Finds all synset nodes containing the specified word
     *
     * @param subgraphRoot root of the sub-graph to search in
     * @param word         word to find
     * @return set of {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode}
     */
    public Set<SynsetNode> findInSubgraph(SynsetNode subgraphRoot, String word) {
        return graphs.get(subgraphRoot).find(word);
    }

    /**
     * Finds all synset nodes having the specified one as their parent
     *
     * @param subgraphRoot root of the sub-graph to search in
     * @param startNode    synset node in the graph from which to start the serach
     * @return set of {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode}
     */
    public Set<SynsetNode> wordsInSubgraphFrom(SynsetNode subgraphRoot, SynsetNode startNode) {
        return graphs.get(subgraphRoot).getAllFrom(startNode);
    }

    /**
     * Finds all synset nodes in the forest that contain the specified word
     *
     * @param word search term
     * @return set of synset nodes
     */
    public Set<SynsetNode> find(String word) {
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();

        for (SynsetGraph s : graphs.values()) {
            Set<SynsetNode> res = s.find(word);
            if (res != null) resultSet.addAll(res);
        }

        return resultSet;
    }

    /**
     * Finds nodes at the specified level. If level is 0, it returns nodes only with in degree 0, if 1 it returns neighbours
     * of those nodes, if 2 neighbours of neighbours etc.
     *
     * @param subgraphRoots graph to explore
     * @param level         until which level to go
     * @return set of {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode} that are found at the specified level
     */
    protected Set<SynsetNode> nodesAtLevel(Set<SynsetNode> subgraphRoots, int level) {
        Set<SynsetNode> roots = new HashSet<SynsetNode>(subgraphRoots);

        for (int i = 1; i < (level + 1); i++) {
            Set<SynsetNode> nextLevel = new HashSet<SynsetNode>();
            for (SynsetNode start : roots) {
                if (start.getChildren().isEmpty()) {
                    nextLevel.add(start);
                } else {
                    nextLevel.addAll(start.getChildren());
                }
            }
            roots.clear();
            roots.addAll(nextLevel);
        }
        return roots;
    }

    /**
     * Finds the set of least common ancestors
     *
     * @param root  root of the sub-graph
     * @param nodes nodes for which LCAs should be found
     * @return set of LCAs
     */
    public Set<SynsetNode> getLCA(SynsetNode root, Set<SynsetNode> nodes) {
        return graphs.get(root).getLCA(nodes);
    }

    /**
     * Lazy evaluation of the subgraph size
     *
     * @param root
     * @return
     */
    public int getSubgraphSize(SynsetNode root) {
        if (subgraphSizes.containsKey(root)) return subgraphSizes.get(root);

        int cnt = 0;
        BFSHierarchicalNode bfs = new BFSHierarchicalNode(root);
        while (bfs.hasNext()) {
            bfs.next();
            cnt++;
        }

        subgraphSizes.put(root, cnt);
        return cnt;
    }

    /**
     * Gets all possbile children words following the WordNet hyponym graph
     *
     * @param word search term
     * @return set of possible child words
     */
    public Set<String> childWords(final String word) {
        Set<SynsetNode> nounRoots = findSubgraphs(word);
        Set<String> possibleNouns = new HashSet<String>();

        List<Future<Set<String>>> workers = new LinkedList<Future<Set<String>>>();
        // for every sub-graph
        for (final SynsetNode n : nounRoots) {
            Future<Set<String>> future = ActivityOpsExecutor.get().submit(new Callable<Set<String>>() {
                @Override
                public Set<String> call() throws Exception {
                    Set<String> subgraphPossibleNouns = new HashSet<String>();
                    Set<SynsetNode> nounNodes = findInSubgraph(n, word);
                    // get the list of synset nodes containing the word
                    for (SynsetNode node : nounNodes) {
                        Set<SynsetNode> childNodes = wordsInSubgraphFrom(n, node);
                        // for every node find its children
                        for (SynsetNode cn : childNodes) subgraphPossibleNouns.addAll(cn.getSynset());
                    }
                    return subgraphPossibleNouns;
                }
            });
            workers.add(future);
        }

        try {
            for (Future<Set<String>> futSets : workers)
                possibleNouns.addAll(futSets.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return possibleNouns;
    }

    public Map<SynsetNode, SynsetGraph> getGraphs() {
        return graphs;
    }
}