package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.classifier.ActivityCluster;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;

import java.util.Map;

/**
 * @author Ivan Gavrilović
 */
public interface ActivityProvider {
    Map<String, ActivityCluster> read(SynsetForest verbs, SynsetForest nouns);
    boolean write(Map<String, ActivityCluster> activities);
}
