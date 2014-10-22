package io.mem0r1es.activitysubsumer.synsets;

import io.mem0r1es.activitysubsumer.utils.HierarchicalStructure;

import java.util.Set;

/**
 * Interface for synset node.
 * @author Ivan Gavrilović
 */
public abstract class SynsetNode extends HierarchicalStructure<SynsetNode>{

    /**
     * Get the code of this synset
     * @return synset code
     */
    public abstract int getCode();

    /**
     * Get the words contained in this synset
     * @return synset words
     */
    public abstract Set<String> getSynset();

    /**
     * Check if this synset contains the specifed word
     * @param word word to find
     * @return {@code true} if this synset contains the word, {@code false} otherwise
     */
    public abstract boolean contains(String word);
}
