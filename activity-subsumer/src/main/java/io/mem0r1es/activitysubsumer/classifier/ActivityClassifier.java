package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.*;


/**
 * @author Ivan Gavrilović
 */
public class ActivityClassifier {
    // keys in the map are location categories from foursquare, and keys in the second map are periods of the day
    private Map<String, ActivityCluster> activities;

    private VerbsSynsetForest verbs;
    private NounsSynsetForest nouns;
    private FoursquareHierarchy hierarchy;

    public ActivityClassifier(VerbsSynsetForest verbs, NounsSynsetForest nouns, FoursquareHierarchy hierarchy) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.hierarchy = hierarchy;
        activities = new HashMap<String, ActivityCluster>();
    }

    public void addActivity(ContextualActivity activity) {
        for (String cat : activity.getLocCategories()) {
            ActivityCluster categoryCluster = activities.get(cat);
            if (categoryCluster == null) {
                categoryCluster = new ActivityCluster(verbs, nouns);
            }
            categoryCluster.addActivity(activity);

            activities.put(cat, categoryCluster);
        }
    }

    public Set<Set<BasicActivity>> subsume(String category) {
        Set<Set<BasicActivity>> allSubgraphSubsumed = new HashSet<Set<BasicActivity>>();

        ActivityCluster categoryCluster = activities.get(category);

        Set<String> allCategories = hierarchy.getHierarchy(category);
        Set<ActivityCluster> allClusters = new HashSet<ActivityCluster>();
        for (String ac : allCategories) {
            allClusters.add(activities.get(ac));
        }

        for (SynsetNode sn : verbs.getGraphs().keySet()) {
            Set<BasicActivity> subsumed = categoryCluster.subsume(allClusters, sn);
            if (!subsumed.isEmpty()) allSubgraphSubsumed.add(subsumed);
        }
        return allSubgraphSubsumed;
    }
}
