package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Reads the csv file containing {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode}
 * as vertices. Direct edge from node A->B means that B is hyponym of A. For instance:
 * (pizza, pizza pie) -- hyponym --> (pizza made with a thick crust)
 *
 * @author Ivan Gavrilović
 */
public class SynsetCSVProvider implements SynsetProvider {
    private Map<Integer, SynsetNode> synsets = null;

    private int numSynsets;

    private InputStream graphStream;
    private InputStream synsetStream;

    /**
     * Creates new {@link SynsetCSVProvider}
     *
     * @param graphStream  path to the file with hyponyms graph
     * @param synsetStream path that contains the mappings from synset codes to words
     */
    public SynsetCSVProvider(InputStream graphStream, InputStream synsetStream, int numSynsets) {
        this.numSynsets = numSynsets;
        this.graphStream = graphStream;
        this.synsetStream = synsetStream;
    }

    /**
     * Triggers the csv file reading, and returns the graph in a adjacency list form
     *
     * @return graph containing {@link SynsetNode} as nodes
     */
    public Set<SynsetNode> read() {
        BufferedReader reader = null;

        Set<SynsetNode> allNodes = new HashSet<SynsetNode>(numSynsets);
        try {
            reader = new BufferedReader(new InputStreamReader(graphStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 1) allNodes.add(codeToSynset(Integer.parseInt(parts[0])));
                else if (parts.length == 2) {
                    SynsetNode fst = codeToSynset(Integer.parseInt(parts[0]));
                    SynsetNode snd = codeToSynset(Integer.parseInt(parts[1]));
                    fst.addChild(snd);
                    snd.addParent(fst);
                    allNodes.add(fst);
                    allNodes.add(snd);
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allNodes;
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
    private Map<Integer, SynsetNode> parseSynsets() {
        BufferedReader reader = null;

        // for each edge, start vertex is synset code, end vertex is the synset member (word)
        Map<Integer, SynsetNode> synsets = new HashMap<Integer, SynsetNode>(numSynsets);
        try {
            reader = new BufferedReader(new InputStreamReader(synsetStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    Integer synsetCode = Integer.parseInt(parts[0]);

                    SynsetNode node = synsets.get(synsetCode);
                    try {
                        String newWord = URLDecoder.decode(parts[1], "UTF-8");
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
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return synsets;
    }
}