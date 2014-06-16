package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityClassifier;
import io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetForest;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.*;
import io.mem0r1es.activitysubsumer.recomm.*;
import io.mem0r1es.activitysubsumer.utils.SubsumerConfig;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.wordnet.Dict;
import io.mem0r1es.activitysubsumer.wordnet.SynsetStore;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.BufferedInputStream;
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
            long start = System.currentTimeMillis();

            SynsetProvider verbsProvider = new SynsetCSVProvider(new FileInputStream(SubsumerConfig.VERBS_SYNSET), new FileInputStream(SubsumerConfig.VERBS_WORDS),
                    new FileInputStream(SubsumerConfig.VERBS_CHILDREN), new FileInputStream(SubsumerConfig.VERBS_PARENTS), SynsetStore.VERBS, Dict.VERBS);
                VerbsSynsetForest verbs = new VerbsSynsetForest(verbsProvider);

            SynsetProvider nounsProvider = new SynsetCSVProvider(new FileInputStream(SubsumerConfig.NOUNS_SYNSET), new FileInputStream(SubsumerConfig.NOUNS_WORDS),
                    new FileInputStream(SubsumerConfig.NOUNS_CHILDREN), new FileInputStream(SubsumerConfig.NOUNS_PARENTS), SynsetStore.NOUNS, Dict.NOUNS);
            NounsSynsetForest nouns = new NounsSynsetForest(nounsProvider);

            CategoryHierarchy hierarchy = new CategoryHierarchy(new FoursquareCategoriesCSV(new FileInputStream(SubsumerConfig.CATEGORIES_CSV)));
            CategoryHierarchy.get();
            ActivityProvider provider = new ActivityFileProvider(new File(SubsumerConfig.ACTIVITIES_FILE));
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

            Set<ContextualActivity> defActs = new DefaultActivitiesProvider(new BufferedInputStream(
                    new FileInputStream(SubsumerConfig.ACTIVITIES_DEFAULT))).read();
            defActs.addAll(classifier.getAllActivities());

            Set<ContextualParameter> params = new HashSet<ContextualParameter>();
            params.add(TimeParameter.get(TimeOfDay.AFTERNOON));
            params.add(new LocationParameter("Food"));
            params.add(new DurationParameter(100));

            ActivityRecognizer recognizer = new ActivityRecognizer(classifier.getAllActivities(), params);
            for(ContextualActivity ca: recognizer.candidates())
            logger.info("Candidate: "+ca.getVerb()+" "+ca.getNoun());


            start = System.currentTimeMillis();
            logger.info("Find: " + classifier.findActivities("have", "food"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
