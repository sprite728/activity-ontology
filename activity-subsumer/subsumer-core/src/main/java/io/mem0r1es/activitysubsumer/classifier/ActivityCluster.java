package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.concurrent.ActivityOpsExecutor;
import io.mem0r1es.activitysubsumer.concurrent.LCAFinder;
import io.mem0r1es.activitysubsumer.concurrent.SubgraphEvaluator;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.synsets.SynsetNode;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Activities clusters. It stores activities based on the verbs sub-graphs
 *
 * @author Ivan Gavrilović
 */
public class ActivityCluster {
    static Logger logger = Logger.getLogger(ActivityCluster.class.getCanonicalName());

    Map<SynsetNode, Set<ContextualActivity>> activities;
    /**
     * Forests of verbs and nouns
     */
    private SynsetForest verbs;
    private SynsetForest nouns;

    private ActivityOpsExecutor executor;

    /**
     * Creates a new activity cluster
     *
     * @param verbs      verbs forest
     * @param nouns      nouns forest
     * @param activities nouns forest
     */
    public ActivityCluster(SynsetForest verbs, SynsetForest nouns, Map<SynsetNode, Set<ContextualActivity>> activities) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.activities = activities;

        this.executor = ActivityOpsExecutor.get();
    }

    /**
     * Creates a new activity cluster
     *
     * @param verbs verbs forest
     * @param nouns nouns forest
     */
    public ActivityCluster(SynsetForest verbs, SynsetForest nouns) {
        this(verbs, nouns, new HashMap<SynsetNode, Set<ContextualActivity>>());
    }

    /**
     * Adds activity to the clusters. It will add the activity to any cluster matching the activity verb
     *
     * @param activity activity to be added
     */
    public void addActivity(ContextualActivity activity) {
        Set<SynsetNode> subgraphs = verbs.findSubgraphs(activity.getVerb());
        for (SynsetNode s : subgraphs) {
            Set<ContextualActivity> verbActivities = activities.get(s);
            if (verbActivities == null) {
                verbActivities = new HashSet<ContextualActivity>();
            }

            verbActivities.add(activity);
            activities.put(s, verbActivities);
        }
    }

    /**
     * Subsume the specified clusters (including {@code this} one) by subsuming the activities in the specified verbs sub-graph
     *
     * @param clusters clusters to subsume
     * @param root     verb sub-graph root
     * @return set of activities that subsume everything, or empty if there are no such activities
     */
    public Set<BasicActivity> subsume(Set<ActivityCluster> clusters, SynsetNode root) {
        Set<ContextualActivity> subgraphActivities = activities.get(root);
        // if nothing to subsume return empty set
        if (subgraphActivities == null || subgraphActivities.isEmpty()) return new HashSet<BasicActivity>();

        // find all sub-graphs containing the same activities
        Set<SynsetNode> subgraphRoots = findActivityClusters(subgraphActivities);
        // set of mandatory nouns which we must find in a noun sub-graph
        Set<String> mandatoryNouns = getActivitiesNouns(subgraphActivities);

        Map<SubgraphEvaluator, Future<Double>> workers = new HashMap<SubgraphEvaluator, Future<Double>>();
        // iterate over every verb subgraph - noun subgraph pair, and find the ones with the max score
        for (SynsetNode subRoot : subgraphRoots) {

            // get nouns from this one
            Set<String> clusterNouns = getActivitiesNouns(activities.get(subRoot));
            // get nouns from all others
            for (ActivityCluster c : clusters) {
                if (c != null && c.activities != null) {
                    clusterNouns.addAll(getActivitiesNouns(c.activities.get(subRoot)));
                }
            }

            for (SynsetNode nounSubgraph : nouns.getGraphs().keySet()) {
                SubgraphEvaluator evaluator = new SubgraphEvaluator(mandatoryNouns, clusterNouns, nounSubgraph, nouns, subRoot);

                workers.put(evaluator, executor.submit(evaluator));
            }
        }

        // find the ones with the highest scores
        List<SynsetNode> bestVerbs = new LinkedList<SynsetNode>();
        List<SynsetNode> bestNouns = new LinkedList<SynsetNode>();
        double maxScore = Double.MIN_VALUE;
        try {
            for (SubgraphEvaluator se : workers.keySet()) {
                double current = workers.get(se).get();

                if (maxScore <= current) {
                    if (maxScore < current) {
                        maxScore = current;
                        bestVerbs.clear();
                        bestNouns.clear();
                    }
                    bestVerbs.add(se.getVerbRoot());
                    bestNouns.add(se.getNounRoot());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generateActivities(bestVerbs, bestNouns, getActivitiesVerbs(subgraphActivities), mandatoryNouns);
    }

    /**
     * Generates the set containing {@link io.mem0r1es.activitysubsumer.activities.BasicActivity} activities
     *
     * @param verbSynsets roots of the verbs subgraphs to use
     * @param nounSynsets roots of the nouns subgraphs to use
     * @param nounsToFind set of nouns that we are looking for
     * @return set containing the generated activities
     */
    public Set<BasicActivity> generateActivities(List<SynsetNode> verbSynsets, List<SynsetNode> nounSynsets,
                                                 Set<String> verbsToFind, Set<String> nounsToFind) {
        Set<BasicActivity> generated = new HashSet<BasicActivity>();
        if (verbSynsets.isEmpty() || nounSynsets.isEmpty()) return generated;

        List<Future<Set<BasicActivity>>> workers = new LinkedList<Future<Set<BasicActivity>>>();
        for (int i = 0; i < nounSynsets.size(); i++) {
            SynsetNode bestVerb = verbSynsets.get(i);
            SynsetNode bestNoun = nounSynsets.get(i);

            LCAFinder worker = new LCAFinder(verbs, nouns, verbsToFind, nounsToFind, bestVerb, bestNoun);
            workers.add(executor.submit(worker));
        }

        try {
            for (Future<Set<BasicActivity>> lca : workers) {
                generated.addAll(lca.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generated;
    }

    /**
     * Finds all {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode} nodes that are keys to the activity graphs
     * (in the cluster mapping) that contain all of the specified activities
     *
     * @param acts set of activities to find in cluster
     * @return keys of the cluster map
     */
    private Set<SynsetNode> findActivityClusters(Set<ContextualActivity> acts) {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode s : activities.keySet()) {
            Set<ContextualActivity> subgraphActivities = activities.get(s);

            if (subgraphActivities != null && subgraphActivities.containsAll(acts)) {
                roots.add(s);
            }
        }

        return roots;
    }

    /**
     * Get set of nouns from specified activities
     *
     * @param activities activities
     * @return set nouns
     */
    private Set<String> getActivitiesNouns(Set<ContextualActivity> activities) {
        Set<String> nouns = new HashSet<String>();
        if (activities == null) return nouns;
        for (ContextualActivity a : activities) nouns.add(a.getNoun());
        return nouns;
    }

    /**
     * Get set of verbs from specified activities
     *
     * @param activities activities
     * @return set verbs
     */
    private Set<String> getActivitiesVerbs(Set<ContextualActivity> activities) {
        Set<String> verbs = new HashSet<String>();
        if (activities == null) return verbs;
        for (ContextualActivity a : activities) verbs.add(a.getVerb());
        return verbs;
    }

    /**
     * Finds all activities that are children to the activity with the specified verb and noun
     *
     * @param possibleVerbs all possible verbs of the activity
     * @param possibleNouns all possible nouns of the activity
     * @return set of activities that match the search terms
     */
    public Set<ContextualActivity> findActivities(Set<String> possibleVerbs, Set<String> possibleNouns) {
        HashSet<ContextualActivity> resultActivities = new HashSet<ContextualActivity>();

        for (Set<ContextualActivity> subActivities : activities.values()) {

            for (ContextualActivity aa : subActivities) {
                if (possibleVerbs.contains(aa.getVerb()) && possibleNouns.contains(aa.getNoun())) resultActivities.add(aa);
            }

        }

        return resultActivities;
    }

    public boolean alreadyProcessed(SynsetNode toProcess, Set<SynsetNode> processed) {
        Set<ContextualActivity> subgraphActivities = activities.get(toProcess);
        if (subgraphActivities == null) return true;

        for (SynsetNode s : processed) {
            Set<ContextualActivity> otherSubgraph = activities.get(s);

            if (subgraphActivities.equals(otherSubgraph)) return true;
        }

        return false;
    }

    public Map<SynsetNode, Set<ContextualActivity>> getActivities() {
        return activities;
    }

    public void setActivities(Map<SynsetNode, Set<ContextualActivity>> activities) {
        this.activities = activities;
    }

    public Set<ContextualActivity> getAllActivities() {
        Set<ContextualActivity> resultSet = new HashSet<ContextualActivity>();
        for (Set<ContextualActivity> cas : activities.values()) {
            resultSet.addAll(cas);
        }
        return resultSet;
    }
}
