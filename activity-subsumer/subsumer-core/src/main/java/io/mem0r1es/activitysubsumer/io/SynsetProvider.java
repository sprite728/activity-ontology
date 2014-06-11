package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNodeImpl;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public interface SynsetProvider {
    SynsetNodeImpl readWithCode(int code);

    Set<SynsetNode> rootSynsets();
}
