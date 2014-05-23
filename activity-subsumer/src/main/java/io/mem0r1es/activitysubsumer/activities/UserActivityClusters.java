package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetUtils;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.Map.Entry;

/**
 * Handles the {@link io.mem0r1es.activitysubsumer.activities.UserActivity} clusters
 * and the noun/verb sub-graphs. Maintains a mapping between the sub-graphs and the corresponding
 * activities.
 *
 * @author Horia Radu
 */
public class UserActivityClusters {
    private Set<TermGraph> verbGraphs;
    private Set<TermGraph> nounGraphs;

    private Map<Long, Set<UserActivity>> activityClusters = new HashMap<Long, Set<UserActivity>>();

    public UserActivityClusters(Set<TermGraph> verbGraphs, Set<TermGraph> nounGraphs) {
        this.verbGraphs = verbGraphs;
        this.nounGraphs = nounGraphs;
    }

    /**
     * Add an activity and connect it to the appropriate cluster(s).
     *
     * @param activity
     *            the activity to be added
     */
    public void addActivity(UserActivity activity) {
        String verb = activity.getVerb();
        for (TermGraph graph : verbGraphs) {
            if (graph.containsNonSenseTerm(verb)) {
                Set<UserActivity> correspondingActivities = activityClusters.get(graph.getID());
                if (correspondingActivities == null) {
                    correspondingActivities = new HashSet<UserActivity>();
                    activityClusters.put(graph.getID(), correspondingActivities);
                }
                correspondingActivities.add(activity);
            }
        }
    }

    /**
     * Add the activity to the given set of clusters. To only be used when building the clusters
     * from previously stored information.
     *
     * @param activity
     *            the activity to be added
     * @param clusterIDs
     *            the set of cluster IDs to which the activity is added
     */
    public void addActivity(UserActivity activity, Set<Long> clusterIDs) {
        for (long clusterID : clusterIDs) {
            Set<UserActivity> correspondingActivities = activityClusters.get(clusterID);
            if (correspondingActivities == null) {
                correspondingActivities = new HashSet<UserActivity>();
                activityClusters.put(clusterID, correspondingActivities);
            }
            correspondingActivities.add(activity);
        }
    }

    /**
     * Return the set of {@link UserActivity} which subsume the given set of activities.
     *
     * @param activities
     *            set of activities which we want to subsume
     * @return set of general activities, which subsume the input activities
     */
    public Set<UserActivity> subsume(Set<UserActivity> activities) {
        Set<UserActivity> result = new HashSet<UserActivity>();
        long bestVerbSubGraphID = -1;
        long bestNounSubGraphID = -1;
        double bestOverallScore = 0;

        // set of mandatory nouns which we must find in a noun sub-graph
        Set<String> mandatoryNouns = new HashSet<String>();
        for (UserActivity activity : activities) {
            mandatoryNouns.add(activity.getNoun());
        }

        // for each activity cluster (verb sub-graph)
        for (Entry<Long, Set<UserActivity>> entry : activityClusters.entrySet()) {
            if (entry.getValue().containsAll(activities)) {
                // if we find all the given activities
                long verbSubGraphID = entry.getKey();

                // set of nouns of activities in this cluster - see how many of these we find in a
                // noun sub-graph
                Set<String> nounsInVerbCluster = new HashSet<String>();
                for (UserActivity activity : entry.getValue()) {
                    nounsInVerbCluster.add(activity.getNoun());
                }

                for (TermGraph nounGraph : nounGraphs) {
                    // temporary set of the mandatory nouns
                    Set<String> mandatoryNounsTemp = new HashSet<String>(mandatoryNouns);

                    // count of how many nouns from the activity cluster we find in the noun
                    // sub-graph
                    double count = 0;

                    Set<String> words = nounGraph.getWords();
                    // all the noun senses in the sub-graph for the given nouns
                    Set<SynsetNode> foundNounSenses =
                            nounGraph.getSensesForNonSenseTerms(nounsInVerbCluster);
                    for (SynsetNode node : foundNounSenses) {
                        for (String word : node.getSynset()) {
                            String wordNoSense = WordNetUtils.wordName(word);
                            // remove from mandatory nouns (if it's the case)
                            mandatoryNounsTemp.remove(wordNoSense);
                            if (nounsInVerbCluster.contains(wordNoSense)) {
                                // if this is a sense of the nouns in the cluster
                                count++;
                            }
                        }
                    }

                    // score is 0 if we didn't find all the mandatory nouns
                    double score = mandatoryNounsTemp.isEmpty() ? count / words.size() : 0;

                    // keep the best verb sub-graph and the corresponding noun sub-graph
                    if (bestOverallScore < score) {
                        bestOverallScore = score;
                        bestVerbSubGraphID = verbSubGraphID;
                        bestNounSubGraphID = nounGraph.getID();
                    }
                }
            }
        }

        if (bestVerbSubGraphID == -1 || bestNounSubGraphID == -1) {
            return result;
        }

        TermGraph bestVerbSubGraph = null;
        TermGraph bestNounSubGraph = null;
        for (TermGraph graph : verbGraphs) {
            if (graph.getID() == bestVerbSubGraphID) {
                bestVerbSubGraph = graph;
            }
        }
        for (TermGraph graph : nounGraphs) {
            if (graph.getID() == bestNounSubGraphID) {
                bestNounSubGraph = graph;
            }
        }

        // get the set of destinations
        Set<String> mandatoryVerbs = new HashSet<String>();
        for (UserActivity activity : activities) {
            mandatoryVerbs.add(activity.getVerb());
        }
        Set<SynsetNode> destinationVerbs =
                bestVerbSubGraph.getNodesForNonSenseTerms(mandatoryVerbs);
        Set<SynsetNode> destinationNouns =
                bestNounSubGraph.getNodesForNonSenseTerms(mandatoryNouns);

        // find all the paths to those destinations
        PathBuilder<SynsetNode, DefaultEdge> verbPaths =
                new PathBuilder<SynsetNode, DefaultEdge>(bestVerbSubGraph,
                        bestVerbSubGraph.getRoot(), destinationVerbs);
        PathBuilder<SynsetNode, DefaultEdge> nounPaths =
                new PathBuilder<SynsetNode, DefaultEdge>(bestNounSubGraph,
                        bestNounSubGraph.getRoot(), destinationNouns);

        // for each possible combination, build the activities
        for (SynsetNode verbLCA : verbPaths.getLCA(destinationVerbs)) {
            for (SynsetNode nounLCA : nounPaths.getLCA(destinationNouns)) {
                for (String verb : verbLCA.getSynset()) {
                    for (String noun : nounLCA.getSynset()) {
                        result.add(new UserActivity("0", verb, noun));
                    }
                }
            }
        }
        return result;
    }

    public Map<Long, Set<UserActivity>> getActivityMapping() {
        return activityClusters;
    }

    /**
     * Find the set of activities which are "specializations" of the given general activity.
     *
     * @param generalActivity
     *            the general activity
     * @return the set of specialized activities
     */
    public Set<UserActivity> findActivities(UserActivity generalActivity) {
        System.out.println("start findActivities()");
        Set<UserActivity> result = new HashSet<UserActivity>();

        String generalVerb = generalActivity.getVerb();
        String generalNoun = generalActivity.getNoun();

        Map<Long, Set<SynsetNode>> nounSubGraphIDToSensesMap =
                new HashMap<Long, Set<SynsetNode>>();
        for (TermGraph nounSubGraph : nounGraphs) {
            Set<SynsetNode> foundSenses = nounSubGraph.getSensesForNonSenseTerm(generalNoun);
            nounSubGraphIDToSensesMap.put(nounSubGraph.getID(), foundSenses);
        }

        for (TermGraph graph : verbGraphs) {
            if (graph.containsNonSenseTerm(generalVerb)) {
                result.addAll(UserActivityClusters.findActivitiesInVerbGraph(	graph.getID(),
                        nounGraphs,
                        activityClusters.get(graph
                                .getID()),
                        nounSubGraphIDToSensesMap));
            }
        }
        return result;
    }

    /**
     * Helper method which returns activities which are more specific than a given general activity.
     * This method will be executed by an AsyncTask.
     *
     * @param verbGraphID
     *            - the id verb graph relative to which we are searching
     * @param nounGraphs
     *            - the set of noun graphs
     * @param activitiesInCluster
     *            - the set of activities in the verb graph. (candidate activities to be returned)
     * @param nounSubGraphIDToSensesMap
     *            - map of senses of the noun of a general activity
     * @return - The set of activities which are reachable, based on the algorithm and the given
     *         senses map.
     */
    protected static Set<UserActivity> findActivitiesInVerbGraph(long verbGraphID,
                                                                 Set<TermGraph> nounGraphs, Set<UserActivity> activitiesInCluster,
                                                                 Map<Long, Set<SynsetNode>> nounSubGraphIDToSensesMap) {
        System.out.println("start findActivitiesInVerbGraph()");
        Set<UserActivity> result = new HashSet<UserActivity>();

        System.out.println("start verb sub-graph" + verbGraphID);
        // set of nouns we want to try to reach
        Set<String> nouns = new HashSet<String>();
        for (UserActivity activity : activitiesInCluster) {
            nouns.add(activity.getNoun());
        }

        for (TermGraph nounSubGraph : nounGraphs) {
            System.out.println("start noun sub-graph" + nounSubGraph.getID());
            Set<SynsetNode> foundSenses = nounSubGraphIDToSensesMap.get(nounSubGraph.getID());
            System.out.println("found senses in noun sub-graph");

            Set<SynsetNode> destinations = nounSubGraph.getSensesForNonSenseTerms(nouns);
            System.out.println("found destinations noun sub-graph");

            for (SynsetNode node : foundSenses) {
                System.out.println("start bfs in noun sub-graph");
                PathBuilder<SynsetNode, DefaultEdge> pathBuilder =
                        new PathBuilder<SynsetNode, DefaultEdge>(nounSubGraph, node, destinations);
                System.out.println("end bfs in noun sub-graph");

                for (SynsetNode destination : destinations) {
                    Set<List<SynsetNode>> pathsToDestination =
                            pathBuilder.getPathsToDestination(destination);
                    if (pathsToDestination != null && !pathsToDestination.isEmpty()) {
                        // we have reached this destination
                        for (UserActivity activity : activitiesInCluster) {
                            for (String word : destination.getSynset()) {
                                if (WordNetUtils.wordName(word).equals(activity.getNoun())) {
                                    result.add(activity);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("end noun sub-graph" + nounSubGraph.getID());
        }
        System.out.println("end verb sub-graph" + verbGraphID);
        return result;
    }
}