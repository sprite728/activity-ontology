package io.mem0r1es.activitysubsumer.wordnet;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.imp.CSVImporter;
import org.jgrapht.imp.IntegerVertexParser;
import org.jgrapht.imp.StringVertexParser;
import org.jgrapht.util.VertexPair;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;

/**
 * Creates {@link org.jgrapht.experimental.dag.DirectedAcyclicGraph} containing {@link SynsetNode}
 * as vertices. Direct edge from node A->B means that B is hyponym of A. For instance:
 * (pizza, pizza pie) -- hyponym --> (pizza made with a thick crust)
 *
 * @author Ivan Gavrilović
 */
public class SynsetGraphBuilder {
    private HashMap<Integer, SynsetNode> synsets = null;
    private String graphPath;
    private String synsetPath;

    /**
     * Creates new {@link SynsetGraphBuilder}
     *
     * @param graphPath  path to the file with hyponyms graph
     * @param synsetPath path that contains the mappings from synset codes to words
     */
    public SynsetGraphBuilder(String graphPath, String synsetPath) {
        this.graphPath = graphPath;
        this.synsetPath = synsetPath;
    }

    /**
     * Triggers the graph creation
     *
     * @return graph containing {@link SynsetNode} as nodes
     */
    public DirectedAcyclicGraph<SynsetNode, DefaultEdge> getGraph() {
        CSVImporter<Integer> importer = new CSVImporter<Integer>(" ", new IntegerVertexParser(), graphPath);
        // get all vertices and edges
        Set<Integer> vertices = importer.getVertices();
        Set<VertexPair<Integer>> edges = importer.getEdges();

        DirectedAcyclicGraph<SynsetNode, DefaultEdge> graph = new DirectedAcyclicGraph<SynsetNode, DefaultEdge>(DefaultEdge.class);
        // now add all to the graph
        for (Integer v : vertices) {
            graph.addVertex(codeToSynset(v));
        }
        for (VertexPair<Integer> edg : edges) {
            try {
                graph.addEdge(codeToSynset(edg.getFirst()), codeToSynset(edg.getSecond()));
            } catch (Exception e) {
                System.out.println("DAG: " + edg);
            }

        }

        return graph;
    }

    /**
     * For the specified code, get the words from the synset
     *
     * @param code synset code
     * @return {@link SynsetNode} containing all the words
     */
    private SynsetNode codeToSynset(Integer code) {
        if (synsets == null) {
            synsets = parseSynsets();
        }
        return synsets.get(code);
    }

    /**
     * Parse the synsets file and make mapping from synset codes to words
     *
     * @return {@link java.util.HashMap} containing the mapping
     */
    private HashMap<Integer, SynsetNode> parseSynsets() {
        CSVImporter<String> importer = new CSVImporter<String>(" ", new StringVertexParser(), synsetPath);
        // each edge is synsetCode --> word
        Set<VertexPair<String>> edges = importer.getEdges();

        // for each edge, start vertex is synset code, end vertex is the synset member (word)
        HashMap<Integer, SynsetNode> synsets = new HashMap<Integer, SynsetNode>();
        for (VertexPair<String> edg : edges) {
            Integer synsetCode = Integer.parseInt(edg.getFirst());

            SynsetNode node = synsets.get(synsetCode);
            try {
                String newWord = URLDecoder.decode(edg.getSecond(), "UTF-8");
                if (node == null) {
                    node = new SynsetNode(synsetCode.toString(), newWord);
                } else {
                    node.addWords(newWord);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            synsets.put(synsetCode, node);
        }
        return synsets;
    }
}
