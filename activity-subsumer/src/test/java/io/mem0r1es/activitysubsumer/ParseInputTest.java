package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
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
        NounsSynsetForest nouns = new NounsSynsetForest(Cons.NOUNS_HYPONYM, Cons.NOUNS_SYNSET);

        System.out.println(forest.findSubgraphs("eat"));
        System.out.println(nouns.findSubgraphs("food"));
    }
}
