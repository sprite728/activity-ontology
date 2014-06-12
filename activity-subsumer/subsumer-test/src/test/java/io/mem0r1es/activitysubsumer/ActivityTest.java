package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityClassifier;
import io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.*;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.wordnet.NounDict;
import io.mem0r1es.activitysubsumer.wordnet.NounStore;
import io.mem0r1es.activitysubsumer.wordnet.VerbDict;
import io.mem0r1es.activitysubsumer.wordnet.VerbStore;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
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
            Thread.sleep(10000);
            long free =            Runtime.getRuntime().totalMemory();

            long start = System.currentTimeMillis();

            VerbStore verbStore = new VerbStore(25061, 13256);
            SynsetProvider verbsProvider = new SynsetCSVProvider(new FileInputStream(Cons.VERBS_SYNSET), new FileInputStream(Cons.VERBS_WORDS),
                    new FileInputStream(Cons.VERBS_CHILDREN), new FileInputStream(Cons.VERBS_PARENTS), verbStore, VerbDict.getInstance());
                VerbsSynsetForest verbs = new VerbsSynsetForest(verbsProvider);

            NounStore nounStore = new NounStore(146547, 84505);
            SynsetProvider nounsProvider = new SynsetCSVProvider(new FileInputStream(Cons.NOUNS_SYNSET), new FileInputStream(Cons.NOUNS_WORDS),
                    new FileInputStream(Cons.NOUNS_CHILDREN), new FileInputStream(Cons.NOUNS_PARENTS), nounStore, NounDict.getInstance());
            NounsSynsetForest nouns = new NounsSynsetForest(nounsProvider);

            CategoryHierarchy hierarchy = new CategoryHierarchy(new FoursquareCategoriesCSV(new FileInputStream(Cons.CATEGORIES_CSV)));
            ActivityProvider provider = new ActivityFileProvider(new File(Cons.ACTIVITIES_FILE));
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
            classifier.addActivity(new UserActivity("3", "have", "lasagna", locs, times, "10"));
            classifier.addActivity(new UserActivity("4", "have", "pasta", locs, times, "10"));

            locs.clear();
            locs.add("Gay Bar");

            classifier.addActivity(new UserActivity("0", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("1", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("2", "consume", "pizza", locs, times, "10"));
            classifier.addActivity(new UserActivity("3", "have", "pasta", locs, times, "10"));

            classifier.save();

            for (Set<BasicActivity> ba : classifier.subsume("Steakhouse")) {
                logger.info("[][][][][[][][][]");
                for (BasicActivity bba : ba) {
                    logger.info(bba.getVerb() + " " + bba.getNoun());
                }
            }
            logger.info("Subsumed ms: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            logger.info("Find: " + classifier.findActivities("have", "food"));


            /* Total amount of free memory available to the JVM */
            System.out.println("Used memory (bytes): " +
                    (Runtime.getRuntime().totalMemory() - free));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
