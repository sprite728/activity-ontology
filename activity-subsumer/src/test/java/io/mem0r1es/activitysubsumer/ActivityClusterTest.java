package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityClassifier;
import io.mem0r1es.activitysubsumer.classifier.FoursquareHierarchy;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityFileProvider;
import io.mem0r1es.activitysubsumer.io.ActivityProvider;
import io.mem0r1es.activitysubsumer.io.FoursquareCategoriesCSV;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class ActivityClusterTest {
    @Test
    public void activityTest() {
        VerbsSynsetForest verbs = new VerbsSynsetForest(Cons.VERBS_HYPONYM, Cons.VERBS_SYNSET);
        NounsSynsetForest nouns = new NounsSynsetForest(Cons.NOUNS_HYPONYM, Cons.NOUNS_SYNSET);
        FoursquareHierarchy hierarchy = new FoursquareHierarchy(new FoursquareCategoriesCSV(Cons.CATEGORIES_CSV));
        ActivityProvider provider = new ActivityFileProvider(Cons.ACTIVITIES_FILE);

        ActivityClassifier classifier = new ActivityClassifier(verbs, nouns, hierarchy, provider);
        Set<TimeOfDay> times = new HashSet<TimeOfDay>();
        times.add(TimeOfDay.EVENING);

        Set<String> locs = new HashSet<String>();
        locs.add("Steakhouse");

        classifier.addActivity(new UserActivity("0", "eat", "food", locs, times, "10"));
        classifier.addActivity(new UserActivity("1", "eat", "food", locs, times, "10"));
        classifier.addActivity(new UserActivity("2", "have", "pizza", locs, times, "10"));
        classifier.addActivity(new UserActivity("3", "consume", "pasta", locs, times, "10"));

        locs.clear();
        locs.add("Gay Bar");

        classifier.addActivity(new UserActivity("0", "eat", "food", locs, times, "10"));
        classifier.addActivity(new UserActivity("1", "eat", "food", locs, times, "10"));
        classifier.addActivity(new UserActivity("2", "have", "pizza", locs, times, "10"));
        classifier.addActivity(new UserActivity("3", "consume", "pasta", locs, times, "10"));

        classifier.save();

        for(Set<BasicActivity> ba: classifier.subsume("Steakhouse")){
            System.out.println("[][][][][[][][][]");
            for(BasicActivity bba: ba) {
                System.out.println(bba.getVerb() + " " + bba.getNoun());
            }
        }
    }
}
