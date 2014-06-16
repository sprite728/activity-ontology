package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.HierarchicalStructure;

import java.util.Set;

/**
 * Interface for synset node
 * @author Ivan Gavrilović
 */
public abstract class SynsetNode extends HierarchicalStructure<SynsetNode>{

    public abstract int getCode();

    public abstract Set<String> getSynset();

    public abstract boolean contains(String word);
}
