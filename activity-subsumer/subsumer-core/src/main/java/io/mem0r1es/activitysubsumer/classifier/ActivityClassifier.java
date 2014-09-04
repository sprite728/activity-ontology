package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.concurrent.ActivityClusterInserter;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityProvider;
import io.mem0r1es.activitysubsumer.synsets.SynsetNode;

import java.util.*;


/**
 * Classifies the activities according to the {@link CategoryHierarchy}.
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
    private CategoryHierarchy hierarchy;
    private ActivityProvider provider;

    private static ActivityClassifier ourInstance = null;

    public ActivityClassifier(VerbsSynsetForest verbs, NounsSynsetForest nouns, CategoryHierarchy hierarchy, ActivityProvider provider) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.hierarchy = hierarchy;
        this.provider = provider;
        activities = provider.read(verbs, nouns);

        ourInstance = this;
    }

    public static ActivityClassifier getInstance() throws IllegalStateException {
        if (ourInstance == null)
            throw new IllegalStateException("Initialize by invoking ActivityClassifier.setInstance() first.");
        return ourInstance;
    }

    public static boolean isSet() {
        return ourInstance != null;
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

    public void removeActivity(ContextualActivity ca){
        for (String cat : ca.getLocCategories()) {
            ActivityCluster categoryCluster = activities.get(cat);
            if (categoryCluster == null) {
                break;
            }
            categoryCluster.removeActivity(ca);
        }
    }

    public void editActivity(ContextualActivity ca){
        for (String cat : ca.getLocCategories()) {
            ActivityCluster categoryCluster = activities.get(cat);
            if (categoryCluster == null) {
                break;
            }
            categoryCluster.removeActivity(ca.getId());
            categoryCluster.addActivity(ca);
        }
    }

    /**
     * Subsume all activities of the specified location category. Use activities from the parent location categories in
     * the Foursquare hierarchy to get the support activity set which is used to evaluate the possible
     * subsuming activities.
     * <p/>
     * Subsumed activities are per verbs sub-graph.
     * <p/>
     * subsumed activities.
     *
     * @param locationCat location category to subsume
     * @return sets of subsumed activities, per verbs sub-graph
     */
    public Set<Set<BasicActivity>> subsume(String locationCat) {
        String locationCategory = locationCat.toLowerCase();
        Set<Set<BasicActivity>> allSubgraphSubsumed = new HashSet<Set<BasicActivity>>();

        ActivityCluster categoryCluster = activities.get(locationCategory);

        Set<String> allCategories = hierarchy.getUp(locationCategory);
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

    /**
     * Get all activities.
     * @return all activities
     */
    public Set<ContextualActivity> getAllActivities() {
        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();
        for (String cat : activities.keySet()) {
            resultSet.addAll(getAllActivities(cat, false));
        }
        return resultSet;
    }

    /**
     * Get all activities assigned to the specified category, or that are up/down in the category hierarchy.
     * By specifying the parameter, one can trigger either the first type of retrieval, or the second one.
     * @param cat category in the hierarchy
     * @param allRelated whether to return activities assigned to up/down categories
     * @return set of all activities satisfying the query
     */
    public Set<ContextualActivity> getAllActivities(String cat, boolean allRelated) {
        String category = cat.toLowerCase();
        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();
        if (!allRelated) {
            if (activities.containsKey(category)) return activities.get(category).getAllActivities();
            else return resultSet;
        }

        Set<String> allCategories = hierarchy.getRelated(category);
        for (String c : allCategories) resultSet.addAll(getAllActivities(c, false));
        return resultSet;
    }
}
