package io.mem0r1es.activitysubsumer.concurrent;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityCluster;

/**
 * @author Ivan Gavrilović
 */
public class ActivityClusterInserter extends Thread{

    ActivityCluster cluster;
    ContextualActivity contextualActivity;
    String category;

    public ActivityClusterInserter(ActivityCluster cluster, ContextualActivity contextualActivity, String category) {
        this.cluster = cluster;
        this.contextualActivity = contextualActivity;
        this.category = category;
    }

    @Override
    public void run() {
        cluster.addActivity(contextualActivity);
    }

    public ActivityCluster getCluster() {
        return cluster;
    }

    public String getCategory() {
        return category;
    }
}
