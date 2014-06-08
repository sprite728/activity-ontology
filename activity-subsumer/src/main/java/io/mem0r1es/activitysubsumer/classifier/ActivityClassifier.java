package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.concurrent.ActivityClusterInserter;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityProvider;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.*;


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

    public boolean save() {
        return provider.write(activities);
    }

    /**
     * Adds the activity. Activity is added to all location categories that are specified in
     * {@link io.mem0r1es.activitysubsumer.activities.ContextualActivity#getLocCategories()}
     *
     * @param activity activity to add
     */
    public void addActivity(ContextualActivity activity) {
        List<ActivityClusterInserter> workers = new LinkedList<ActivityClusterInserter>();
        for (String cat : activity.getLocCategories()) {
            ActivityCluster categoryCluster = activities.get(cat);
            if (categoryCluster == null) {
                categoryCluster = new ActivityCluster(verbs, nouns);
            }
            ActivityClusterInserter worker = new ActivityClusterInserter(categoryCluster, activity, cat);
            worker.start();

            workers.add(worker);
        }


        for (ActivityClusterInserter worker : workers) {
            try {
                worker.join();
                activities.put(worker.getCategory(), worker.getCluster());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (!categoryCluster.alreadyProcessed(sn, processedSubgraphs)) {
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

        // all possible child words for the specified verb and noun
        Set<String> childVerbs = verbs.childWords(verb);
        Set<String> childNouns = nouns.childWords(noun);

        for (ActivityCluster ac : activities.values()) {
            resultSet.addAll(ac.findActivities(childVerbs, childNouns));
        }
        return resultSet;
    }

    public Set<ContextualActivity> getAllActivities() {
        System.out.println("Started gathering all activities");
        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();
        for (String cat : activities.keySet()) {
            resultSet.addAll(getAllActivities(cat, false));
        }
        System.out.println("Finished gathering all activities");
        return resultSet;
    }

    public Set<ContextualActivity> getAllActivities(String category, boolean hierarchical) {
        if (!hierarchical) return activities.get(category).getAllActivities();

        Set<String> allCategories = hierarchy.getHierarchy(category);

        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();
        for (String cat : allCategories) resultSet.addAll(getAllActivities(cat, false));
        return resultSet;
    }
}
