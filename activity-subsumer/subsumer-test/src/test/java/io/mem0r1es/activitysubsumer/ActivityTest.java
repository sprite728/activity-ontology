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
import io.mem0r1es.activitysubsumer.synsets.Dict;
import io.mem0r1es.activitysubsumer.synsets.Synsets;
import io.mem0r1es.activitysubsumer.utils.SubConf;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.synsets.SynsetStore;
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

            SynsetAdapter verbsAdapter = SynsetAdapter.defaultVerbs();
            Synsets verbsSynsets = new Synsets(verbsAdapter, SynsetStore.VERBS);
                VerbsSynsetForest verbs = new VerbsSynsetForest(verbsSynsets);

            SynsetAdapter nounsAdapter = SynsetAdapter.defaultNouns();
            Synsets nounsSynsets = new Synsets(nounsAdapter, SynsetStore.NOUNS);
            NounsSynsetForest nouns = new NounsSynsetForest(nounsSynsets);

            CategoryHierarchy hierarchy = new CategoryHierarchy(new FoursquareCategoriesCSV(new FileInputStream(SubConf.CONFIG.getCategoriesCsv())));
            CategoryHierarchy.get();
            ActivityProvider provider = new ActivityFileProvider(new File(SubConf.CONFIG.getActivitiesFile()));
            logger.info("Preparing system ms: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            ActivityClassifier classifier = new ActivityClassifier(verbs, nouns, hierarchy, provider);
            Set<TimeOfDay> times = new HashSet<TimeOfDay>();
            times.add(TimeOfDay.EVENING);

            Set<String> locs = new HashSet<String>();
            locs.add("steakhouse");

            classifier.addActivity(new UserActivity("0", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("1", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("2", "have", "pizza", locs, times, "10"));
            classifier.addActivity(new UserActivity("3", "have", "lasagna", locs, times, "10"));
            classifier.addActivity(new UserActivity("4", "have", "pasta", locs, times, "10"));

            locs.clear();
            locs.add("gay bar");

            classifier.addActivity(new UserActivity("0", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("1", "eat", "food", locs, times, "10"));
            classifier.addActivity(new UserActivity("2", "consume", "pizza", locs, times, "10"));
            classifier.addActivity(new UserActivity("3", "have", "pasta", locs, times, "10"));

            classifier.save();

            for (Set<BasicActivity> ba : classifier.subsume("steakhouse")) {
                logger.info("[][][][][[][][][]");
                for (BasicActivity bba : ba) {
                    logger.info(bba.getVerb() + " " + bba.getNoun());
                }
            }
            logger.info("Subsumed ms: " + (System.currentTimeMillis() - start));

            Set<ContextualActivity> defActs = new DefaultActivitiesProvider(new BufferedInputStream(
                    new FileInputStream(SubConf.CONFIG.getActivitiesDefault()))).read();
            defActs.addAll(classifier.getAllActivities());

            Set<ContextualParameter> params = new HashSet<ContextualParameter>();
            params.add(TimeParameter.get(TimeOfDay.AFTERNOON));
            params.add(new LocationParameter("Food"));
            params.add(new DurationParameter(100));

            ActivityRecognizer recognizer = new ActivityRecognizer(classifier.getAllActivities(), params);
            for(ContextualActivity ca: recognizer.candidates())
            logger.info("Candidate: "+ca.getVerb()+" "+ca.getNoun());

            logger.info("Match: "+ Dict.NOUNS.search("yo"));
            logger.info("Match: "+ Dict.NOUNS.search("aaaa"));
            logger.info("Match: "+ Dict.NOUNS.search("aa"));
            logger.info("Match: "+ Dict.NOUNS.search("zz"));
            logger.info("Match: "+ Dict.NOUNS.search("rol"));
            logger.info("Match: "+ Dict.NOUNS.search("low"));


            start = System.currentTimeMillis();
            logger.info("Find: " + classifier.findActivities("have", "food"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
