package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public interface SynsetProvider {
    /**
     * Get the root synsets of the nouns/verbs
     * @return {@link java.util.Set} containing the root synsets
     */
    Set<SynsetNode> rootSynsets();
}
