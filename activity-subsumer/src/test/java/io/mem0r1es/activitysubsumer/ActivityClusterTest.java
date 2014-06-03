package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityClassifier;
import io.mem0r1es.activitysubsumer.classifier.FoursquareHierarchy;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
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

        ActivityClassifier classifier = new ActivityClassifier(verbs, nouns, hierarchy);
        Set<TimeOfDay> times = new HashSet<TimeOfDay>();
        times.add(TimeOfDay.EVENING);

        Set<String> locs = new HashSet<String>();
        locs.add("Steakhouse");
        UserActivity ua = new UserActivity("0", "eat", "food", locs, times, "10");
        UserActivity ub = new UserActivity("1", "eat", "food", locs, times, "10");

        classifier.addActivity(ua);
        classifier.addActivity(ub);

        for(Set<BasicActivity> ba: classifier.subsume("Steakhouse")){
            for(BasicActivity bba: ba) {
                System.out.println(bba.getVerb() + " " + bba.getNoun());
            }
        }
    }
}
