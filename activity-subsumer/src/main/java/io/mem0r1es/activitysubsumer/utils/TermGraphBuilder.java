package io.mem0r1es.activitysubsumer.utils;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.*;


/**
 * Class responsible for building a sub-graph based on the WordNet network
 *
 * @author Horia Radu
 */
public class TermGraphBuilder {
    private static final String TAG = TermGraphBuilder.class.getCanonicalName();
    private static final String SEPARATOR = ";";
    public static int NR_CYCLES = 0;
    public static int NR_SELF_CYCLES = 0;

    /**
     * map of concept name to set of terms with senses which are synonyms
     */
    private static Map<String, Set<String>> synonyms;
    /**
     * set of sub-graphs which are being built
     */
    private static Set<TermGraph> graphs;
    /**
     * the WordNet network
     */
    private static DirectedAcyclicGraph<String, DefaultEdge> wordDag;

    private Set<Set<WordNetNode>> nodesToBeMerged = new HashSet<Set<WordNetNode>>();

    /**
     * reads the sub-graph from a given CSV file
     *
     * @param inCSV input stream
     * @return the set of sub-graphs
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public Set<TermGraph> readFromCSV(String inCSV) throws IOException {
        return readFromCSV(new FileInputStream(inCSV));
    }

    /**
     * reads the sub-graph from a given CSV file
     *
     * @param inputStream input stream
     * @return the set of sub-graphs
     * @throws IOException
     */
    public Set<TermGraph> readFromCSV(InputStream inputStream) throws IOException {
        Set<TermGraph> result = new HashSet<TermGraph>();

        TermGraph current = null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
        for (String line = buf.readLine(); line != null; line = buf.readLine()) {
            if (line.startsWith("#"))
                continue;

            if (line.startsWith("!root")) {
                StringTokenizer tokenizer = new StringTokenizer(line, SEPARATOR);
                tokenizer.nextToken();
                String id = tokenizer.nextToken();
                String key = tokenizer.nextToken();
                current = new TermGraph(vertexFromCSV(key), Long.parseLong(id));
                result.add(current);
            } else {
                String[] tokens = line.split(SEPARATOR);
                WordNetNode source = vertexFromCSV(tokens[1]);
                WordNetNode target = vertexFromCSV(tokens[2]);
                current.addVertex(source);
                current.addVertex(target);
                try {
                    current.addEdge(source, target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        buf.close();

        return result;
    }

    /**
     * based on the given files, the sub-graphs are built and then written in the specified output
     * file
     *
     * @param synonymsFile - file containing the synonym sets
     * @param rootsFile    - file containing the desired sub-graph roots
     * @param dagFile      - file containing the WordNet network
     * @param termsCSV     - file in which the nodes will be written (for Gephi support)
     * @param edgesCSV     - file in which the edges will be written (for Gephi support)
     * @param outCSV       - file in which the set of sub-graphs will be written
     * @throws IOException
     */
    public void buildFiles(String synonymsFile, String rootsFile, String dagFile, String termsCSV,
                           String edgesCSV, String outCSV) throws IOException {
        System.out.println("start reading synonyms");
        synonyms = new HashMap<String, Set<String>>();
        BufferedReader buf =
                new BufferedReader(new InputStreamReader(new FileInputStream(synonymsFile)));
        for (String line = buf.readLine(); line != null; line = buf.readLine()) {
            if (line.startsWith("#"))
                continue;

            StringTokenizer tokenizer = new StringTokenizer(line, SEPARATOR);
            String key = tokenizer.nextToken();

            Set<String> syns = new HashSet<String>();
            while (tokenizer.hasMoreTokens()) {
                syns.add(tokenizer.nextToken());
            }
            synonyms.put(key, syns);
        }
        buf.close();
        System.out.println("end reading synonyms");

        System.out.println("start reading sub graph roots");
        graphs = new HashSet<TermGraph>();
        buf = new BufferedReader(new InputStreamReader(new FileInputStream(rootsFile)));
        for (String line = buf.readLine(); line != null; line = buf.readLine()) {
            if (line.startsWith("#"))
                continue;

            StringTokenizer tokenizer = new StringTokenizer(line, SEPARATOR);
            String key = tokenizer.nextToken();

            for (Set<String> words : getSynonyms(key)) {
                WordNetNode root = new WordNetNode(words);
                TermGraph termGraph = new TermGraph(root);
                graphs.add(termGraph);
            }
        }
        buf.close();
        System.out.println("end reading sub graph roots");

        System.out.println("start reading dag");
        buf = new BufferedReader(new InputStreamReader(new FileInputStream(dagFile)));
        wordDag = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String line = buf.readLine(); line != null; line = buf.readLine()) {
            if (line.startsWith("#"))
                continue;

            StringTokenizer tokenizer = new StringTokenizer(line, SEPARATOR);
            String node = tokenizer.nextToken();
            wordDag.addVertex(node);

            while (tokenizer.hasMoreTokens()) {
                String next = tokenizer.nextToken();
                wordDag.addVertex(next);
                wordDag.addEdge(node, next);
            }
        }
        buf.close();
        System.out.println("start reading dag");

        StringBuilder vertices = new StringBuilder("Id\n");
        StringBuilder edges = new StringBuilder("Source;Target\n");

        System.out.println("start building sub-graphs");
        for (TermGraph graph : graphs) {
            System.out.println("start building sub-graph " + graph.getID());
            nodesToBeMerged.clear();

            for (String word : graph.getRoot().getWords()) {
                dfs(word, graph);
            }
            mergeNodes(graph, nodesToBeMerged);

            System.out.println("end building sub-graph " + graph.getID());
            vertices.append("!");
            vertices.append(graph.getID());
            vertices.append(vertexSetToCSV(graph));
            vertices.append("!!");
            vertices.append(graph.getID());

            edges.append("!");
            edges.append(graph.getID());
            edges.append(edgeSetToCSV(graph));
            edges.append("!!");
            edges.append(graph.getID());
            System.out.println("end writing sub-graph " + graph.getID());
        }
        System.out.println("end building sub-graphs");

        StringBuilder outBuilder = new StringBuilder();
        for (TermGraph graph : graphs) {
            outBuilder.append("!root");
            outBuilder.append(SEPARATOR);
            outBuilder.append(graph.getID());
            outBuilder.append(SEPARATOR);
            outBuilder.append(vertexToCSV(graph.getRoot()));
            outBuilder.append("\n");
            for (DefaultEdge edge : graph.edgeSet()) {
                outBuilder.append(graph.getID());
                outBuilder.append(SEPARATOR);
                outBuilder.append(vertexToCSV(graph.getEdgeSource(edge)));
                outBuilder.append(SEPARATOR);
                outBuilder.append(vertexToCSV(graph.getEdgeTarget(edge)));
                outBuilder.append("\n");
            }
        }

        FileWriter writer = new FileWriter(new File(termsCSV));
        writer.write(vertices.toString());
        writer.close();
        writer = new FileWriter(new File(edgesCSV));
        writer.write(edges.toString());
        writer.close();
        writer = new FileWriter(new File(outCSV));
        writer.write(outBuilder.toString());
        writer.close();

        System.out.println(NR_CYCLES);
        System.out.println(NR_SELF_CYCLES);
    }

    @SuppressWarnings("unused")
    public Set<TermGraph> getTermSubGraphs() {
        return graphs;
    }

    /**
     * Method which handles merging of the nodes in case there were cycles encountered in the
     * construction process.
     *
     * @param graph           the graph
     * @param nodesToBeMerged the paths which represent the encountered cycles
     */
    private void mergeNodes(TermGraph graph, Set<Set<WordNetNode>> nodesToBeMerged) {
        // collapse the sets until there is nothing more to be done. This is done in order to handle
        // multiple intersecting paths
        while (collapseSets(nodesToBeMerged))
            ;

        for (Set<WordNetNode> nodes : nodesToBeMerged) {
            Set<WordNetNode> sources = new HashSet<WordNetNode>();
            Set<WordNetNode> targets = new HashSet<WordNetNode>();
            Set<String> words = new HashSet<String>();

            for (WordNetNode node : nodes) {
                for (DefaultEdge edge : graph.edgesOf(node)) {
                    WordNetNode edgeSource = graph.getEdgeSource(edge);
                    WordNetNode edgeTarget = graph.getEdgeTarget(edge);
                    if (edgeSource.equals(node)) {
                        if (!nodes.contains(edgeTarget)) {
                            targets.add(edgeTarget);
                        }
                    } else if (edgeTarget.equals(node)) {
                        if (!nodes.contains(edgeSource)) {
                            sources.add(edgeSource);
                        }
                    }
                }

                words.addAll(node.getWords());
            }

            for (WordNetNode node : nodes) {
                graph.removeVertex(node);
            }
            WordNetNode node = new WordNetNode(words);
            graph.addVertex(node);
            for (WordNetNode source : sources) {
                graph.addEdge(source, node);
            }
            for (WordNetNode target : targets) {
                graph.addEdge(node, target);
            }
        }
    }

    /**
     * Does one traversal of the input set and merges two members if they are not disjoint. After
     * one traversal in which a merge was done, it stops
     *
     * @param nodesToBeMerged the set of nodes to ber merged
     * @return true, if a merge was done, false otherwise
     */
    private boolean collapseSets(Set<Set<WordNetNode>> nodesToBeMerged) {
        Set<Set<WordNetNode>> result = new HashSet<Set<WordNetNode>>();
        Set<Set<WordNetNode>> toBeRemoved = new HashSet<Set<WordNetNode>>();

        boolean merge = false;

        for (Set<WordNetNode> set : nodesToBeMerged) {
            Set<WordNetNode> mergedSet = new HashSet<WordNetNode>();
            mergedSet.addAll(set);
            result.add(mergedSet);
            toBeRemoved.add(set);

            for (Set<WordNetNode> set2 : nodesToBeMerged) {
                if (!set.equals(set2)) {
                    if (!disjoint(mergedSet, set2)) {
                        merge = true;
                        mergedSet.addAll(set2);
                        toBeRemoved.add(set2);
                    }
                }
            }

            if (merge) {
                break;
            }
        }

        nodesToBeMerged.removeAll(toBeRemoved);
        nodesToBeMerged.addAll(result);
        return merge;
    }

    /**
     * @param set  the first set
     * @param set2 the second set
     * @return true if the sets are disjoint, false otherwise
     */
    private boolean disjoint(Set<WordNetNode> set, Set<WordNetNode> set2) {
        for (WordNetNode node : set) {
            if (set2.contains(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the set of synonym sets in which the given key is present
     *
     * @param key the word which must be present
     * @return the set of synonym sets
     */
    private Set<Set<String>> getSynonyms(String key) {
        Set<Set<String>> words = new HashSet<Set<String>>();
        for (Set<String> synonyms : TermGraphBuilder.synonyms.values()) {
            if (synonyms.contains(key)) {
                words.add(synonyms);
            }
        }

        if (words.isEmpty()) {
            words.add(Collections.singleton(key));
        }
        return words;
    }

    /**
     * Performs a depth first traversal of the WordNet graph and builds the given termGraph. It adds
     * all the nodes which can be reached from the given vertex in the WordNet graph, recursively.
     *
     * @param vertex    the current vertex
     * @param termGraph the term graph
     */
    private void dfs(String vertex, TermGraph termGraph) {
        Set<WordNetNode> nodes = termGraph.getNodes(vertex);
        for (WordNetNode node : nodes) {
            System.out.println("visiting" + node + " in graph " + termGraph.getID());
        }

        for (DefaultEdge edge : wordDag.edgesOf(vertex)) {
            String target = wordDag.getEdgeTarget(edge);
            String source = wordDag.getEdgeSource(edge);
            if (source.equals(vertex)) {
                // only look at forward edges
                Set<Set<String>> targetSynSets = getSynonyms(target);
                for (Set<String> targetSyns : targetSynSets) {
                    WordNetNode targetNode = new WordNetNode(targetSyns);
                    System.out.println("add node " + targetNode + " to graph " + termGraph.getID());
                    termGraph.addVertex(targetNode);

                    for (WordNetNode node : nodes) {
                        if (!node.equals(targetNode)) {
                            System.out.println("add edge " + node + " -> " + targetNode
                                    + " to graph " + termGraph.getID());
                            try {
                                termGraph.addEdge(node, targetNode);
                            } catch (IllegalArgumentException e) {
                                System.out.println("cycle: " + node + " -> " + targetNode);
                                Set<List<WordNetNode>> pathsToDestination =
                                        new PathBuilder<WordNetNode, DefaultEdge>(termGraph,
                                                targetNode, Collections.singleton(node))
                                                .getPathsToDestination(node);

                                Set<WordNetNode> innerNodes = new HashSet<WordNetNode>();
                                for (List<WordNetNode> path : pathsToDestination) {
                                    System.out.println(path);
                                    innerNodes.addAll(path);
                                }
                                nodesToBeMerged.add(innerNodes);
                                NR_CYCLES++;
                            }
                        }
                    }
                }
                dfs(target, termGraph);
            }
        }
    }

    private String edgeSetToCSV(TermGraph graph) {
        StringBuilder result = new StringBuilder();
        for (DefaultEdge edge : graph.edgeSet()) {
            result.append(vertexToCSV(graph.getEdgeSource(edge)));
            result.append(SEPARATOR);
            result.append(vertexToCSV(graph.getEdgeTarget(edge)));
            result.append("\n");
        }
        return result.toString();
    }

    private String vertexSetToCSV(TermGraph graph) {
        StringBuilder result = new StringBuilder();
        for (WordNetNode node : graph.vertexSet()) {
            result.append(vertexToCSV(node));
            result.append("\n");
        }
        return result.toString();
    }

    private String vertexToCSV(WordNetNode node) {
        StringBuilder line = new StringBuilder();
        for (String word : node.getWords()) {
            if (!line.toString().equals("")) {
                line.append("|");
            }
            line.append(word);
        }
        return line.toString();
    }

    private WordNetNode vertexFromCSV(String key) {
        String[] items = key.split("\\|");
        return new WordNetNode(items);
    }
}