package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.ActivityClusters;
import io.mem0r1es.activitysubsumer.activities.SubsumedActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityIO;
import io.mem0r1es.activitysubsumer.io.WordNetDBInputParser;
import io.mem0r1es.activitysubsumer.utils.Cons;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

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
        ActivityClusters activityClusters = new ActivityClusters(forest, nouns, new ActivityIO(Cons.ACTIVITIES_GRAPH, Cons.ACTIVITIES_MAPPINGS));
        activityClusters.saveClusters();

        System.out.println("Adding activity: started");
        try {
            Scanner s = new Scanner(new File("/Users/ivan/Projects/ontology/activity-subsumer/src/test/resources/activities.graph"));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                try {
                    String verb = tokenizer.nextToken();
                    String noun = tokenizer.nextToken();
                    Set<String> locations = new HashSet<String>();
                    Collections.addAll(locations, tokenizer.nextToken().split(Cons.ENTRY_SEPARATOR_REG));
                    Set<String> times = new HashSet<String>();
                    Collections.addAll(times, tokenizer.nextToken().split(Cons.ENTRY_SEPARATOR_REG));
                    String duration = tokenizer.nextToken();

                    UserActivity userActivity = new UserActivity(Long.toString(System.nanoTime()), verb, noun, locations, times, duration);
                    System.out.println("Adding activity: " + userActivity + ", locations, times " + locations + ", " + times);
                    activityClusters.addActivity(userActivity);
                } catch (Exception e) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activityClusters.addActivity(new SubsumedActivity(Long.toString(System.nanoTime()), "consume", "food"));


        //activityClusters.saveClusters();
    }
}
