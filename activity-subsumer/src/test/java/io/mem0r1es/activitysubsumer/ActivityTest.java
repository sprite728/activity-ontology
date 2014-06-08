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
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class ActivityTest {
    static Logger logger = Logger.getLogger(ActivityTest.class);
    @Test
    public void testActivities() {
        try {
            long start = System.currentTimeMillis();
            VerbsSynsetForest verbs = new VerbsSynsetForest(new FileInputStream(Cons.VERBS_HYPONYM), new FileInputStream(Cons.VERBS_SYNSET));
            NounsSynsetForest nouns = new NounsSynsetForest(new FileInputStream(Cons.NOUNS_HYPONYM), new FileInputStream(Cons.NOUNS_SYNSET));
            FoursquareHierarchy hierarchy = new FoursquareHierarchy(new FoursquareCategoriesCSV(new FileInputStream(Cons.CATEGORIES_CSV)));
            ActivityProvider provider = new ActivityFileProvider(new FileInputStream(Cons.ACTIVITIES_FILE), new PrintWriter(Cons.ACTIVITIES_FILE));
            logger.info("Preparing system ms: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

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


            for (Set<BasicActivity> ba : classifier.subsume("Steakhouse")) {
                logger.info("[][][][][[][][][]");
                for (BasicActivity bba : ba) {
                    logger.info(bba.getVerb() + " " + bba.getNoun());
                }
            }
            logger.info("Subsumed ms: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            logger.info("Find: " + classifier.findActivities("eat", "nutrient"));
            logger.info("Find ms: " + (System.currentTimeMillis() - start));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
