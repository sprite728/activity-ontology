package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.utils.Cons;
import org.junit.Test;

import java.io.FileInputStream;

/**
 * @author Ivan Gavrilović
 */
public class ParseInputTest {
    @Test
    public void testNouns() {
        try {
            //new WordNetDBInputParser().parseInput(new FileInputStream(Cons.DB_NOUN_IN), new FileInputStream(Cons.NOUNS));
            //new WordNetDBInputParser().parseInput(new FileInputStream(Cons.DB_VERB_IN), new FileInputStream(Cons.VERBS));

            VerbsSynsetForest forest = new VerbsSynsetForest(new FileInputStream(Cons.VERBS_HYPONYM), new FileInputStream(Cons.VERBS_SYNSET));
            NounsSynsetForest nouns = new NounsSynsetForest(new FileInputStream(Cons.NOUNS_HYPONYM), new FileInputStream(Cons.NOUNS_SYNSET));

            System.out.println(forest.findSubgraphs("eat"));
            System.out.println(nouns.findSubgraphs("food"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
