package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.HierarchicalStructure;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public abstract class SynsetNode extends HierarchicalStructure<SynsetNode>{

    public abstract String getCode();

    public abstract Set<String> getSynset();

    public abstract boolean contains(String word);

    public abstract boolean containsAny(Set<String> words);

    public abstract void addWords(String... words);
}
