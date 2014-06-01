package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;
import io.mem0r1es.activitysubsumer.activities.ActivityClusters;
import io.mem0r1es.activitysubsumer.activities.SubsumedActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityIO;
import io.mem0r1es.activitysubsumer.utils.Cons;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class ActivityClusterTest {
    @Test
    public void activityTest() {
        VerbsSynsetForest verbs = new VerbsSynsetForest(Cons.VERBS_HYPONYM, Cons.VERBS_SYNSET);
        NounsSynsetForest nouns = new NounsSynsetForest(Cons.NOUNS_HYPONYM, Cons.NOUNS_SYNSET);

        ActivityClusters activityClusters = new ActivityClusters(verbs, nouns, new ActivityIO(Cons.ACTIVITIES_GRAPH, Cons.ACTIVITIES_MAPPINGS));
        SubsumedActivity sa = new SubsumedActivity(Long.toString(System.nanoTime()), "have", "pizza");
        activityClusters.addActivity(sa);
        UserActivity ua = new UserActivity(Long.toString(System.nanoTime()), "eat", "pizza", "food stand", "noon|evening", "1h2h");
        activityClusters.addActivity(ua);

        Set<AbstractActivity> toSubsume = new HashSet<AbstractActivity>();
        toSubsume.add(ua); toSubsume.add(sa);
        Set<AbstractActivity> subAct = activityClusters.subsume(toSubsume);

        System.out.println("Subsumed: ");
        for(AbstractActivity a:subAct){
            System.out.println(a.getVerb()+" - "+a.getNoun());
        }

        System.out.println("Find activities for have-food: ");
        for(AbstractActivity a:activityClusters.findActivities("have", "food")){
            System.out.println(a.getVerb()+" - "+a.getNoun());
        }

        activityClusters.saveClusters();
    }
}
