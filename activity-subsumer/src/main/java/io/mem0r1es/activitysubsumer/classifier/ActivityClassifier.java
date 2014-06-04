package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityProvider;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Classifies the activities according to the {@link io.mem0r1es.activitysubsumer.classifier.FoursquareHierarchy}.
 * Each activity is added to the location category at which it occurred.
 * Activities from the specified location category can be generalized, and all activities can be searched by
 * verb and noun.
 *
 * @author Ivan Gavrilović
 */
public class ActivityClassifier {
    // keys in the map are location categories from foursquare, and keys in the second map are periods of the day
    private Map<String, ActivityCluster> activities;

    private VerbsSynsetForest verbs;
    private NounsSynsetForest nouns;
    private FoursquareHierarchy hierarchy;
    private ActivityProvider provider;

    public ActivityClassifier(VerbsSynsetForest verbs, NounsSynsetForest nouns, FoursquareHierarchy hierarchy, ActivityProvider provider) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.hierarchy = hierarchy;
        this.provider = provider;
        activities = provider.read(verbs, nouns);
    }

    public boolean save(){
        return  provider.write(activities);
    }

    /**
     * Adds the activity. Activity is added to all location categories that are specified in
     * {@link io.mem0r1es.activitysubsumer.activities.ContextualActivity#getLocCategories()}
     *
     * @param activity activity to add
     */
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

    /**
     * Subsume all activities of the specified location category. Use activities from the parent location categories in
     * the Foursquare hierarchy to get the support activity set which is used to evaluate the possible.
     * <p/>
     * Subsumed activities are per verbs sub-graph.
     * <p/>
     * subsumed activities.
     *
     * @param locationCategory location category to subsume
     * @return sets of subsumed activities, per verbs sub-graph
     */
    public Set<Set<BasicActivity>> subsume(String locationCategory) {
        Set<Set<BasicActivity>> allSubgraphSubsumed = new HashSet<Set<BasicActivity>>();

        ActivityCluster categoryCluster = activities.get(locationCategory);

        Set<String> allCategories = hierarchy.getHierarchy(locationCategory);
        Set<ActivityCluster> allClusters = new HashSet<ActivityCluster>();
        for (String ac : allCategories) {
            allClusters.add(activities.get(ac));
        }

        Set<SynsetNode> processedSubgraphs = new HashSet<SynsetNode>();
        for (SynsetNode sn : verbs.getGraphs().keySet()) {
            if (!categoryCluster.alreadyProcessed(sn, processedSubgraphs)){
                Set<BasicActivity> subsumed = categoryCluster.subsume(allClusters, sn);
                if (!subsumed.isEmpty()) allSubgraphSubsumed.add(subsumed);
            }

            processedSubgraphs.add(sn);
        }
        return allSubgraphSubsumed;
    }

    /**
     * Find all activities whose verb and noun are the ones specified, or the verb and noun are their hyponym
     *
     * @param verb verbs to find
     * @param noun noun to find
     * @return set of {@link io.mem0r1es.activitysubsumer.activities.ContextualActivity} that match the search terms
     */
    public Set<ContextualActivity> findActivities(String verb, String noun) {
        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();

        for (ActivityCluster ac : activities.values()) {
            resultSet.addAll(ac.findActivities(verb, noun));
        }
        return resultSet;
    }
}
