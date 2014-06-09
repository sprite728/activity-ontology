package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public interface SynsetProvider {
    public Set<SynsetNode> read();
}
