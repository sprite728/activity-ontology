package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivityClusters;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TermGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class TermGraphBuilderTest {

    @Test
    public void main() throws IOException {
        new TermGraphBuilder().buildFiles(Cons.NOUN_SYNONYMS, Cons.NOUN_ROOTS, Cons.NOUNS_GRAPH, Cons.NOUN_NODES_GEPHI, Cons.NOUN_EDGES_GEPHI, Cons.NOUN_OUT);
        new TermGraphBuilder().buildFiles(Cons.VERB_SYNONYMS, Cons.VERB_ROOTS, Cons.VERBS_GRAPH, Cons.VERB_NODES_GEPHI, Cons.VERB_EDGES_GEPHI, Cons.VERB_OUT);

        Set<TermGraph> nounGraphs = new TermGraphBuilder().readFromCSV(Cons.NOUN_OUT);
        Set<TermGraph> verbSubGraphs = new TermGraphBuilder().readFromCSV(Cons.VERB_OUT);
        UserActivityClusters activityClusters = new UserActivityClusters(verbSubGraphs, nounGraphs);

        System.out.println(new Date() + " " + "start reading activities");
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Cons.ACTIVITIES)));

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(new Date() + " " + "start add activity");
            StringTokenizer tok = new StringTokenizer(line, ",");
            String verb = tok.nextToken();
            String noun = tok.nextToken();
            String location = tok.nextToken();
            String timeOfDay = tok.nextToken();
            String avgDur = tok.nextToken();

            String[] locations = location.split(Cons.ENTRY_SEPARATOR);
            Set<String> locationsSet = new HashSet<String>();
            Collections.addAll(locationsSet, locations);

            String[] timeOfDays = timeOfDay.split(Cons.ENTRY_SEPARATOR);
            Set<String> timesOfDaySet = new HashSet<String>();
            Collections.addAll(timesOfDaySet, timeOfDays);

            UserActivity act = new UserActivity(verb, noun, locationsSet, timesOfDaySet, avgDur);
            activityClusters.addActivity(act);
            System.out.println(new Date() + " " + "end add activity");
        }
        br.close();

        long stopTime = System.currentTimeMillis();
        Object elapsedTime = stopTime - startTime;
        System.out.println(new Date() + " " + "Add activities Time:" + elapsedTime);

        Map<UserActivity, Set<Long>> reverseMapping = new HashMap<UserActivity, Set<Long>>();
        for (Entry<Long, Set<UserActivity>> entry : activityClusters.getActivityMapping().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().size());

            for (UserActivity activity : entry.getValue()) {
                Set<Long> set = reverseMapping.get(activity);
                if (set == null) {
                    set = new HashSet<Long>();
                    reverseMapping.put(activity, set);
                }
                set.add(entry.getKey());
            }
        }

        System.out.println(reverseMapping.size());

        for (Entry<UserActivity, Set<Long>> entry : reverseMapping.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().size());
        }

        startTime = System.currentTimeMillis();
        System.out.println(activityClusters.findActivities(new UserActivity("eat", "food")));
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println(new Date() + " " + "search Time:" + elapsedTime);

        Set<UserActivity> toSubsume = new HashSet<UserActivity>();
//		toSubsume.add(new UserActivity("eat", "bread"));
//		toSubsume.add(new UserActivity("eat", "candy"));

        toSubsume.add(new UserActivity("drink", "brandy"));
        toSubsume.add(new UserActivity("drink", "coffee"));
        startTime = System.currentTimeMillis();
        System.out.println(new Date() + " subsume: " + toSubsume + ": " + activityClusters.subsume(toSubsume));
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println(new Date() + " " + "subsume Time:" + elapsedTime);
    }
}