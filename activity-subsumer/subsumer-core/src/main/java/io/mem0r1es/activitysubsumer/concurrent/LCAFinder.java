package io.mem0r1es.activitysubsumer.concurrent;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Ivan Gavrilović
 */
public class LCAFinder implements Callable<Set<BasicActivity>> {

    private SynsetForest verbs;
    private SynsetForest nouns;
    private Set<String> verbsToFind;
    private Set<String> nounsToFind;
    private SynsetNode bestVerb;
    private SynsetNode bestNoun;

    public LCAFinder(SynsetForest verbs, SynsetForest nouns, Set<String> verbsToFind, Set<String> nounsToFind,
                     SynsetNode bestVerb, SynsetNode bestNoun) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.verbsToFind = verbsToFind;
        this.nounsToFind = nounsToFind;
        this.bestVerb = bestVerb;
        this.bestNoun = bestNoun;
    }

    @Override
    public Set<BasicActivity> call() {
        Set<BasicActivity> generated = new HashSet<BasicActivity>();

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

        for (String v : possibleVerbs) {
            for (String n : possibleNouns) {
                generated.add(new BasicActivity(v, n));
            }
        }
        return generated;
    }
}
