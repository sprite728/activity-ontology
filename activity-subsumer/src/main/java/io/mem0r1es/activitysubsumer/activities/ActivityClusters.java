package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityIO;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * Represents the user activity clusters. It allows addition of new activites and their subsumption.
 *
 * @author Ivan Gavrilović
 */
public class ActivityClusters {
    /**
     * Mapping from verb synset to the activity graphs. Verb synsets are at the same time to roots of verbs subgraphs.
     */
    private Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> clusters;

    /**
     * Used to read and write clusters to persistent storage
     */
    private ActivityIO parser;

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
     * @param parser parser that reads/writes the cluster
     */
    public ActivityClusters(VerbsSynsetForest verbs, NounsSynsetForest nouns, ActivityIO parser) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.parser = parser;
        initClusters();
    }

    private void initClusters() {
        clusters = parser.readGraph();
    }

    public void saveClusters() {
        parser.write(clusters);
    }

    /**
     * Adds activity to the clusters. It will add the activity to any cluster matching the activity verb
     *
     * @param activity activity to be added
     */
    public void addActivity(AbstractActivity activity) {
        Set<SynsetNode> subgraphs = verbs.findSubgraphs(activity.getVerb());
        for (SynsetNode s : subgraphs) {
            DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag = clusters.get(s);
            if (dag == null) {
                dag = new DirectedAcyclicGraph<AbstractActivity, DefaultEdge>(DefaultEdge.class);
            }

            dag.addVertex(activity);
            clusters.put(s, dag);
        }
    }

    /**
     * Subsumes the set of specified activities or returns an empty set if there is no such activity
     *
     * @param activities set of activities to subsume
     * @return set of subsumed activities
     */
    public Set<AbstractActivity> subsume(Set<AbstractActivity> activities) {
        Set<AbstractActivity> subsumed = new HashSet<AbstractActivity>();
        // find the clusters containing these activities
        Set<SynsetNode> clusterRoots = findActivityClusters(activities);
        System.out.println("Cluster roots: " + clusterRoots);
        if (!clusterRoots.isEmpty()) {
            // set of mandatory nouns which we must find in a noun sub-graph
            Set<String> mandatoryNouns = getActivitiesNouns(activities);

            List<SynsetNode> bestVerbs = new LinkedList<SynsetNode>();
            List<SynsetNode> bestNouns = new LinkedList<SynsetNode>();
            double maxScore = Double.MIN_VALUE;

            // iterate over every verb subgraph - noun subgraph pair, and find the ones with the max score
            for (SynsetNode actCluster : clusterRoots) {
                Set<String> clusterNouns = getActivitiesNouns(clusters.get(actCluster).vertexSet());
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

            subsumed = generateActivities(bestVerbs, bestNouns, mandatoryNouns);
        }

        return subsumed;
    }

    /**
     * Generates the set containing {@link io.mem0r1es.activitysubsumer.activities.SubsumedActivity} activities
     *
     * @param verbSynsets roots of the verbs subgraphs to use
     * @param nounSynsets roots of the nouns subgraphs to use
     * @param nounsToFind set of nouns that we are looking for
     * @return set containing the generated activities
     */
    public Set<AbstractActivity> generateActivities(List<SynsetNode> verbSynsets, List<SynsetNode> nounSynsets, Set<String> nounsToFind) {
        Set<AbstractActivity> generated = new HashSet<AbstractActivity>();
        if (verbSynsets.isEmpty() || nounSynsets.isEmpty()) return generated;

        for (int i = 0; i < nounSynsets.size(); i++) {
            SynsetNode bestVerb = verbSynsets.get(i);
            SynsetNode bestNoun = nounSynsets.get(i);

            Set<String> verbsToFind = getActivitiesVerbs(clusters.get(bestVerb).vertexSet());

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
                    generated.add(new SubsumedActivity(Long.toString(System.nanoTime()), v, n));
                }
            }
        }
        return generated;
    }

    /**
     * Finds all {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode} nodes that are keys to the activity graphs
     * (in the cluster mapping) that contain all of the specified activities
     *
     * @param activities set of activities to find in cluster
     * @return keys of the cluster map
     */
    private Set<SynsetNode> findActivityClusters(Set<AbstractActivity> activities) {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode s : clusters.keySet()) {
            DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag = clusters.get(s);

            if (dag != null && !dag.vertexSet().isEmpty()) {
                if (dag.vertexSet().containsAll(activities)) {
                    roots.add(s);
                }
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
    private Set<String> getActivitiesNouns(Set<AbstractActivity> activities) {
        Set<String> nouns = new HashSet<String>();
        for (AbstractActivity a : activities) nouns.add(a.getNoun());
        return nouns;
    }

    /**
     * Get set of verbs from specified activities
     *
     * @param activities activities
     * @return set verbs
     */
    private Set<String> getActivitiesVerbs(Set<AbstractActivity> activities) {
        Set<String> verbs = new HashSet<String>();
        for (AbstractActivity a : activities) verbs.add(a.getVerb());
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
    public Set<AbstractActivity> findActivities(String verb, String noun) {
        HashSet<AbstractActivity> resultActivities = new HashSet<AbstractActivity>();

        // all possible child words for the specified verb and noun
        Set<String> childVerbs = verbs.childWords(verb);

        Set<String> childNouns = nouns.childWords(noun);

        for (DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag : clusters.values()) {
            Set<AbstractActivity> activities = dag.vertexSet();

            for (AbstractActivity aa : activities) {
                if (childVerbs.contains(aa.getVerb()) && childNouns.contains(aa.getNoun())) resultActivities.add(aa);
            }

        }

        return resultActivities;
    }
}
