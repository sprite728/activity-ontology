package io.mem0r1es.activitysubsumer.graphs;

import io.mem0r1es.activitysubsumer.wordnet.SynsetGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.Graphs;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains all of the synset graphs, which are not connected in the WordNet
 *
 * @author Ivan Gavrilović
 */
public class SynsetForest {
    protected Map<SynsetNode, SynsetGraph> graphs = new HashMap<SynsetNode, SynsetGraph>();
    protected Map<SynsetNode, Integer> subgraphSizes = new HashMap<SynsetNode, Integer>();

    /**
     * Generates a forest of synsets
     * @param hyponymPath path to hyponym file (graph file)
     * @param synsetPath path to mapping file, which maps the code from the graph file to synset members
     * @param rootLevel at which level should we split the big graph. If it is 0, only the nodes with in degree 0 are taken,
     *                  if it is 1, the first neighbours are added, if 2, neighbours of neighbours are added, and so on
     */
    public SynsetForest(String hyponymPath, String synsetPath, int rootLevel) {
        init(hyponymPath, synsetPath, rootLevel);
    }

    private void init(String hyponym, String synset, int rootLevel) {
        SynsetGraphBuilder builder = new SynsetGraphBuilder(hyponym, synset);
        DirectedAcyclicGraph<SynsetNode, DefaultEdge> builderGraph = builder.getGraph();

        Set<SynsetNode> roots = nodesAtLevel(builderGraph, rootLevel);
        System.out.println("Number of sub-graphs: "+roots.size());

        // all verbs will share the whole verbs graph
        for (SynsetNode node : roots) {
            VerbsSynsetGraph vsg = new VerbsSynsetGraph(node, builderGraph);
            graphs.put(node, vsg);
        }
    }

    /**
     * Returns the roots of the sub-graphs containing the word
     *
     * @param word search term
     * @return set with roots of the sub-graphs
     */
    public Set<SynsetNode> findSubgraphs(String word) {
        Set<SynsetNode> resultRoots = new HashSet<SynsetNode>();
        for (Map.Entry<SynsetNode, SynsetGraph> entry : graphs.entrySet()) {
            if (entry.getValue().contains(word)) {
                resultRoots.add(entry.getKey());
            }
        }
        return resultRoots;
    }

    /**
     * Finds all synset nodes containing any of the specified words
     * @param subgraphRoot root of the sub-graph to search in
     * @param words words to find
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode}
     */
    public Set<SynsetNode> findAllInSubgraph(SynsetNode subgraphRoot, Set<String> words){
        return graphs.get(subgraphRoot).findAll(words);
    }

    /**
     * Finds all synset nodes containing the specified word
     * @param subgraphRoot root of the sub-graph to search in
     * @param word word to find
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode}
     */
    public Set<SynsetNode> findInSubgraph(SynsetNode subgraphRoot, String word){
        return graphs.get(subgraphRoot).find(word);
    }

    /**
     * Finds all synset nodes having the specified one as their parent
     * @param subgraphRoot root of the sub-graph to search in
     * @param startNode synset node in the graph from which to start the serach
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode}
     */
    public Set<SynsetNode> wordsInSubgraphFrom(SynsetNode subgraphRoot, SynsetNode startNode){
        return graphs.get(subgraphRoot).getAllFrom(startNode);
    }

    /**
     * Finds all synset nodes in the forest that contain the specified word
     * @param word search term
     * @return set of synset nodes
     */
    public Set<SynsetNode> find(String word){
        Set<SynsetNode> resultSet = new HashSet<SynsetNode>();

        for(SynsetGraph s:graphs.values()){
            resultSet.addAll(s.find(word));
        }

        return resultSet;
    }

    /**
     * Finds nodes at the specified level. If level is 0, it returns nodes only with in degree 0, if 1 it returns neighbours
     * of those nodes, if 2 neighbours of neighbours etc.
     * @param graph graph to explore
     * @param level until which level to go
     * @return set of {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} that are found at the specified level
     */
    private Set<SynsetNode> nodesAtLevel(DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph, int level) {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();

        for (SynsetNode s : graph.vertexSet()) {
            if (graph.inDegreeOf(s) == 0) {
                roots.add(s);
            }
        }

        for(int i = 1; i < (level + 1); i++){
            Set<SynsetNode> nextLevel = new HashSet<SynsetNode>();
            for(SynsetNode start: roots){
                nextLevel.addAll(Graphs.neighborListOf(graph, start));
            }
            roots.addAll(nextLevel);
        }
        return roots;
    }

    /**
     * Finds the set of least common ancestors
     * @param root root of the sub-graph
     * @param nodes nodes for which LCAs should be found
     * @return set of LCAs
     */
    public Set<SynsetNode> getLCA(SynsetNode root, Set<SynsetNode> nodes){
        System.out.println("Finding LCA for: " + nodes);
        return graphs.get(root).getLCA(nodes);
    }

    /**
     * Lazy evaluation of the subgraph size
     * @param root
     * @return
     */
    public int getSubgraphSize(SynsetNode root){
        if (subgraphSizes.containsKey(root)) return subgraphSizes.get(root);

        BreadthFirstIterator<SynsetNode, DefaultEdge> bfs = new BreadthFirstIterator<SynsetNode, DefaultEdge>(graphs.get(root).getGraph(), root);
        int cnt = 0;
        while (bfs.hasNext()){
            cnt++; bfs.next();
        }

        subgraphSizes.put(root, cnt);
        return cnt;
    }

    /**
     * Gets all possbile children words following the WordNet hyponym graph
     * @param word search term
     * @return set of possible child words
     */
    public Set<String> childWords(String word){
        Set<SynsetNode> nounRoots = findSubgraphs(word);
        Set<String> possibleNouns = new HashSet<String>();

        // for every sub-graph
        for(SynsetNode n: nounRoots){
            Set<SynsetNode> nounNodes = findInSubgraph(n, word);
            // get the list of synset nodes containing the word
            for(SynsetNode node:nounNodes){
                Set<SynsetNode> childNodes = wordsInSubgraphFrom(n, node);
                // for every node find its children
                for(SynsetNode cn: childNodes) possibleNouns.addAll(cn.getSynset());
            }
        }
        return possibleNouns;
    }

    public Map<SynsetNode, SynsetGraph> getGraphs() {
        return graphs;
    }
}