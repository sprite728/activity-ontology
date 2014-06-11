package io.mem0r1es.activitysubsumer.concurrent;

import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNodeImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Ivan Gavrilović
 */
public class SubgraphEvaluator implements Callable<Double> {

    private Set<String> mandatoryWords;
    private Set<String> clusterWords;
    private SynsetNode nounRoot;
    private SynsetForest forest;
    private SynsetNode verbRoot;


    /**
     * Evaluates the sub-graph according to its alignment with the words found in the cluster.
     *
     * The score is {@value 0} if not all mandatory words are found; otherview is it
     * {@code num_words_in_subgraph_that_are_in_cluster / total_number_of_words_in_subgrah}
     *
     * @param mandatoryWords set of words that must be found, otherwise, the score is {@code 0}
     * @param clusterWords   set of words that are within the activity cluster; for instance set of nouns
     * @param nounRoot   root of the verbs/nouns subgraph that we are evaluating
     */
    public SubgraphEvaluator(Set<String> mandatoryWords, Set<String> clusterWords, SynsetNode nounRoot, SynsetForest forest, SynsetNode verbRoot) {
        this.mandatoryWords = mandatoryWords;
        this.clusterWords = clusterWords;
        this.nounRoot = nounRoot;
        this.forest = forest;

        this.verbRoot = verbRoot;
    }

    @Override
    public Double call() {
        // find all the synsets that contain any of the nouns from the cluster
        Set<SynsetNode> clusterSynsets = forest.findAllInSubgraph(nounRoot, clusterWords);

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
        return cnt / (1.0 * forest.getSubgraphSize(nounRoot));
    }

    public SynsetNode getNounRoot() {
        return nounRoot;
    }

    public SynsetNode getVerbRoot() {
        return verbRoot;
    }
}
