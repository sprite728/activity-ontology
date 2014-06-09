package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.SynsetCSVProvider;
import io.mem0r1es.activitysubsumer.io.SynsetProvider;
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

            SynsetProvider verbsProvider = new SynsetCSVProvider(new FileInputStream(Cons.VERBS_HYPONYM), new FileInputStream(Cons.VERBS_SYNSET), 13789);
            VerbsSynsetForest verbs = new VerbsSynsetForest(verbsProvider);

            SynsetProvider nounsProvider = new SynsetCSVProvider(new FileInputStream(Cons.NOUNS_HYPONYM), new FileInputStream(Cons.NOUNS_SYNSET), 82192);
            NounsSynsetForest nouns = new NounsSynsetForest(nounsProvider);

            System.out.println(verbs.findSubgraphs("eat"));
            System.out.println(nouns.findSubgraphs("food"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
