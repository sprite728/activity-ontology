package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityIO;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.naming.Context;
import java.util.*;

/**
 * Activities clusters. It stores activities based on the verbs sub-graphs
 *
 * @author Ivan Gavrilović
 */
public class ActivityCluster {
    Map<SynsetNode, Set<ContextualActivity>> activities = new HashMap<SynsetNode, Set<ContextualActivity>>();

    /**
     * Forests of verbs and nouns
     */
    private VerbsSynsetForest verbs;
    private NounsSynsetForest nouns;

    /**
     * Creates a new activity cluster
     *
     * @param verbs  verbs forest
     * @param nouns  nouns forest
     */
    public ActivityCluster(VerbsSynsetForest verbs, NounsSynsetForest nouns) {
        this.verbs = verbs;
        this.nouns = nouns;
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
     * @param clusters clusters to subsume
     * @param root verb sub-graph root
     * @return set of activities that subsume everything, or empty if there are no such activities
     */
    public Set<BasicActivity> subsume(Set<ActivityCluster> clusters, SynsetNode root){
        Set<ContextualActivity> subgraphActivities = activities.get(root);

        if (subgraphActivities == null || subgraphActivities.isEmpty()) return new HashSet<BasicActivity>();

        Set<SynsetNode> subgraphRoots = findActivityClusters(subgraphActivities);

        // set of mandatory nouns which we must find in a noun sub-graph
        Set<String> mandatoryNouns = getActivitiesNouns(subgraphActivities);

        List<SynsetNode> bestVerbs = new LinkedList<SynsetNode>();
        List<SynsetNode> bestNouns = new LinkedList<SynsetNode>();
        double maxScore = Double.MIN_VALUE;

        // iterate over every verb subgraph - noun subgraph pair, and find the ones with the max score
        for (SynsetNode subRoot : subgraphRoots) {

            // get nouns from this one
            Set<String> clusterNouns =  getActivitiesNouns(activities.get(subRoot));
            // get nouns from all others
            for(ActivityCluster c: clusters){
                if (c != null && c.activities != null) {
                    clusterNouns.addAll(getActivitiesNouns(c.activities.get(subRoot)));
                }
            }

            for (SynsetNode nounSubgraph : nouns.getGraphs().keySet()) {

                double current = evaluateSubgraph(mandatoryNouns, clusterNouns, nounSubgraph, nouns);

                if (maxScore <= current) {
                    if (maxScore < current) {
                        maxScore = current;
                        bestVerbs.clear();
                        bestNouns.clear();
                    }
                    bestVerbs.add(subRoot);
                    bestNouns.add(nounSubgraph);
                }
            }
        }
        return generateActivities(bestVerbs, bestNouns, mandatoryNouns, getActivitiesVerbs(subgraphActivities));
    }

    /**
     * Subsumes the set of specified activities or returns an empty set if there is no such activity
     *
     * @param acts set of activities to subsume
     * @return set of subsumed activities
     */
    public Set<BasicActivity> subsume(Set<ContextualActivity> acts) {
        Set<BasicActivity> subsumed = new HashSet<BasicActivity>();
        // find the clusters containing these activities
        Set<SynsetNode> clusterRoots = findActivityClusters(acts);

        if (!clusterRoots.isEmpty()) {
            // set of mandatory nouns which we must find in a noun sub-graph
            Set<String> mandatoryNouns = getActivitiesNouns(acts);

            List<SynsetNode> bestVerbs = new LinkedList<SynsetNode>();
            List<SynsetNode> bestNouns = new LinkedList<SynsetNode>();
            double maxScore = Double.MIN_VALUE;

            // iterate over every verb subgraph - noun subgraph pair, and find the ones with the max score
            for (SynsetNode actCluster : clusterRoots) {
                Set<String> clusterNouns = getActivitiesNouns(activities.get(actCluster));
                for (SynsetNode nounSubgraph : nouns.getGraphs().keySet()) {

                    double current = evaluateSubgraph(mandatoryNouns, clusterNouns, nounSubgraph, nouns);

                    if (maxScore <= current) {
                        if (maxScore < current) {
                            maxScore = current;
                            bestVerbs.clear();
                            bestNouns.clear();
                        }
                        bestVerbs.add(actCluster);
                        bestNouns.add(nounSubgraph);
                    }
                }
            }

            subsumed = generateActivities(bestVerbs, bestNouns, mandatoryNouns, getActivitiesVerbs(acts));
        }

        return subsumed;
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
                                                 Set<String> nounsToFind, Set<String> verbsToFind) {
        Set<BasicActivity> generated = new HashSet<BasicActivity>();
        if (verbSynsets.isEmpty() || nounSynsets.isEmpty()) return generated;

        for (int i = 0; i < nounSynsets.size(); i++) {
            SynsetNode bestVerb = verbSynsets.get(i);
            SynsetNode bestNoun = nounSynsets.get(i);

            Set<SynsetNode> matchVerb = verbs.findAllInSubgraph(bestVerb, verbsToFind);
            Set<SynsetNode> matchNouns = nouns.findAllInSubgraph(bestNoun, nounsToFind);

            Set<String> possibleVerbs = new HashSet<String>();
            for (SynsetNode s : verbs.getLCA(bestVerb, matchVerb)) {
                possibleVerbs.addAll(s.getSynset());
            }
            Set<String> possibleNouns = new HashSet<String>();
            for (SynsetNode s : nouns.getLCA(bestNoun, matchNouns)) {
                possibleNouns.addAll(s.getSynset());
            }
            System.out.println("Combine verbs - nouns: " + possibleVerbs + " - " + possibleNouns);
            for (String v : possibleVerbs) {
                for (String n : possibleNouns) {
                    generated.add(new BasicActivity(v, n));
                }
            }
        }
        return generated;
    }

    /**
     * Finds all {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} nodes that are keys to the activity graphs
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
     * Evaluates the subgraph according to he
     *
     * @param mandatoryWords set of words that must be found, otherwise, the score is {@code 0}
     * @param clusterWords   set of words that are within the activity cluster; for instance set of nouns
     * @param subgraphRoot   root of the verbs/nouns subgraph that we are evaluating
     * @return the score is {@value 0} if not all mandatory words are found; otherview is it
     * {@code num_words_in_subgraph_that_are_in_cluster / total_number_of_words_in_subgrah}
     */
    private double evaluateSubgraph(Set<String> mandatoryWords, Set<String> clusterWords, SynsetNode subgraphRoot, SynsetForest forest) {
        // find all the synsets that contain any of the nouns from the cluster
        Set<SynsetNode> clusterSynsets = forest.findAllInSubgraph(subgraphRoot, clusterWords);

        Set<String> synsetWords = new HashSet<String>();
        for (SynsetNode s : clusterSynsets) {
            synsetWords.addAll(s.getSynset());
        }

        // check that all mandatory
        for (String m : mandatoryWords) {
            if (!synsetWords.contains(m)) {
                return Double.MIN_VALUE;
            }
        }

        int cnt = 0;
        for (String cw : clusterWords) {
            if (clusterWords.contains(cw)) cnt++;
        }
        return cnt / (1.0 * forest.getSubgraphSize(subgraphRoot));
    }

    /**
     * Finds all activities that are children to the activity with the specified verb and noun
     * @param verb verb of the activity
     * @param noun noun of the activity
     * @return set of activities that match the search terms
     */
    public Set<ContextualActivity> findActivities(String verb, String noun) {
        HashSet<ContextualActivity> resultActivities = new HashSet<ContextualActivity>();

        // all possible child words for the specified verb and noun
        Set<String> childVerbs = verbs.childWords(verb);

        Set<String> childNouns = nouns.childWords(noun);

        for (Set<ContextualActivity> subActivities : activities.values()) {

            for (ContextualActivity aa : subActivities) {
                if (childVerbs.contains(aa.getVerb()) && childNouns.contains(aa.getNoun())) resultActivities.add(aa);
            }

        }

        return resultActivities;
    }
}
