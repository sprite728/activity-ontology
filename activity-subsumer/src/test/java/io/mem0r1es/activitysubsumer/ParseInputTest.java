package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.ActivityClusters;
import io.mem0r1es.activitysubsumer.activities.SubsumedActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetGraph;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.utils.Cons;
import org.testng.annotations.Test;

/**
 * @author Ivan Gavrilović
 */
public class ParseInputTest {
    @Test
    public void nounsTest() {
        //new WordNetDBInputParser().parseInput(Cons.DB_NOUN_IN, Cons.NOUNS);
        //new WordNetDBInputParser().parseInput(Cons.DB_VERB_IN, Cons.VERBS);

        VerbsSynsetForest forest = new VerbsSynsetForest(Cons.VERBS_HYPONYM, Cons.VERBS_SYNSET);
        NounsSynsetGraph nouns = new NounsSynsetGraph(Cons.NOUNS_HYPONYM, Cons.NOUNS_SYNSET);

        System.out.println(forest.find("eat"));
        System.out.println(nouns.find("food"));


        ActivityClusters activityClusters = new ActivityClusters(forest, nouns, Cons.ACTIVITIES_GRAPH, Cons.ACTIVITIES_MAPPINGS);
        activityClusters.saveClusters();

        ActivityClusters activityClusters3 = new ActivityClusters(forest, nouns, Cons.ACTIVITIES_GRAPH, Cons.ACTIVITIES_MAPPINGS);
        activityClusters.addActivity(new SubsumedActivity(Long.toString(System.nanoTime()), "consume", "food"));
        activityClusters.saveClusters();
    }
}
