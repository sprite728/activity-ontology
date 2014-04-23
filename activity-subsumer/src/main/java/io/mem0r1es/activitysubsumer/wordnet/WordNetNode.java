package io.mem0r1es.activitysubsumer.wordnet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Node in the WordNet sub-graphs. A node contains multiple words (with senses) based on synonym
 * sets and on eventual cycles in the WordNet graph.
 * </p>
 * <p>
 * Example usage:
 * <br />
 * {@code new WordNet("manner.n.01", "mode.n.01", "style.n.01", "way.n.01", "fashion.n.01")}
 * </p>
 * @author Horia Radu
 */
public class WordNetNode {
    /**
     * set of synonyms
     */
    private final Set<String> words = new HashSet<String>();

    public WordNetNode(String... words) {
        Collections.addAll(this.words, words);
    }

    public WordNetNode(Set<String> words) {
        this.words.addAll(words);
    }

    public void addWords(String... words) {
        Collections.addAll(this.words, words);
    }

    public boolean synonyms(WordNetNode other) {
        for (String word : words) {
            if (other.words.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((words == null) ? 0 : words.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordNetNode other = (WordNetNode) obj;
        if (words == null) {
            if (other.words != null)
                return false;
        } else if (!words.equals(other.words))
            return false;
        return true;
    }

    public boolean hasWord(String word) {
        return words.contains(word);
    }

    public Set<String> getWords() {
        return words;
    }

    @Override
    public String toString() {
        return words.toString();
    }
}