package io.mem0r1es.activitysubsumer.wordnet;

import com.sun.deploy.net.URLEncoder;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Synset is the node in the WordNet sub-graphs. A node contains multiple words (with senses) based on synonym
 * sets and on eventual cycles in the WordNet graph.
 * </p>
 * <p>
 * Example usage:
 * <br />
 * {@code new SynsetNode("04936080-n", "manner", "mode", "style", "way", "fashion")}
 * </p>
 * @author Horia Radu
 *
 * Changes: Ivan Gavrilovic
 */
public class SynsetNode {
    /**
     * Words that belong to this synset
     */
    private final Set<String> synset = new HashSet<String>();
    /**
     * Synset code, as retrieved by WordNet
     */
    private String code;


    public SynsetNode(String code, String... synset) {
        this.code = code;
        Collections.addAll(this.synset, synset);
    }

    public SynsetNode(String code, Set<String> synset) {
        this.code = code;
        this.synset.addAll(synset);
    }

    public void addWords(String... words) {
        Collections.addAll(this.synset, words);
    }

    public boolean hasSharedWords(SynsetNode other) {
        for (String word : synset) {
            if (other.synset.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynsetNode that = (SynsetNode) o;

        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    public boolean contains(String word) {
        return synset.contains(word);
    }

    public String getCode() {
        return code;
    }

    public Set<String> getSynset() {
        return synset;
    }

    @Override
    public String toString() {
        String print = code;
        for (String s: synset){
            try {
                print += " " + URLEncoder.encode(s, "UTF-8");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return print;
    }

    public static SynsetNode deSerialize(String input){
        String parts[] = input.split("\\s");
        String code = parts[0];
        Set<String> words = new HashSet<String>();

        try {
            for (int i = 1; i < parts.length; i++) words.add(URLDecoder.decode(parts[i], "UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new SynsetNode(code, words);
    }
}