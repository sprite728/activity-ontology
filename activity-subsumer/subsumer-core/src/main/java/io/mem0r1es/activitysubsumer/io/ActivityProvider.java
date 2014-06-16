package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.classifier.ActivityCluster;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;

import java.util.Map;

/**
 * Should be implemented by all classes that fetch the initial activities, and that persist the activities.
 *
 * @author Ivan Gavrilović
 */
public interface ActivityProvider {
    /**
     * Reads the data, and clusters the activities according to the verb and noun sub-graphs
     *
     * @param verbs {@link io.mem0r1es.activitysubsumer.graphs.SynsetForest} containing all verb sub-graphs
     * @param nouns {@link io.mem0r1es.activitysubsumer.graphs.SynsetForest} containing all noun sub-graphs
     * @return {@link java.util.Map} containing the location categories as keys, and
     * {@link io.mem0r1es.activitysubsumer.classifier.ActivityCluster} as values
     */
    Map<String, ActivityCluster> read(SynsetForest verbs, SynsetForest nouns);

    /**
     * Writes the specified activities to permanent storage
     *
     * @param activities activities to be written
     * @return result of the operation, {@code true} if success, {@code false} otherwise
     */
    boolean write(Map<String, ActivityCluster> activities);
}
