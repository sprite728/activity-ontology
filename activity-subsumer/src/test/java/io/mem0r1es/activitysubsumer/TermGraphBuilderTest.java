package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivityClusters;
import io.mem0r1es.activitysubsumer.utils.TermGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.testng.annotations.Test;

@Test
public class TermGraphBuilderTest {
	private static final String NOUNS_GRAPH = "src/test/resources/nouns.graph";
	private static final String NOUN_SYNONYMS = "src/test/resources/noun_senses.txt";
	private static final String NOUN_ROOTS = "src/test/resources/noun_roots";
	private static final String NOUN_NODES_GEPHI = "out/nouns_nodes.csv";
	private static final String NOUN_EDGES_GEPHI = "out/nouns_edges.csv";
	private static final String NOUN_OUT = "out/nouns_out_syn_final.csv";

	private static final String VERBS_GRAPH = "src/test/resources/verbs.graph";
	private static final String VERB_SYNONYMS = "src/test/resources/verb_senses.txt";
	private static final String VERB_ROOTS = "src/test/resources/verb_roots";
	private static final String VERB_NODES_GEPHI = "out/verbs_nodes.csv";
	private static final String VERB_EDGES_GEPHI = "out/verbs_edges.csv";
	private static final String VERB_OUT = "out/verbs_out_syn_final.csv";

	@Test
	public void main() throws IOException {
		// new TermGraphBuilder().buildFiles(NOUN_SYNONYMS, NOUN_ROOTS, NOUNS_GRAPH,
		// NOUN_NODES_GEPHI, NOUN_EDGES_GEPHI, NOUN_OUT);
		// new TermGraphBuilder().buildFiles(VERB_SYNONYMS, VERB_ROOTS, VERBS_GRAPH,
		// VERB_NODES_GEPHI, VERB_EDGES_GEPHI, VERB_OUT);

		Set<TermGraph> nounGraphs = new TermGraphBuilder().readFromCSV(NOUN_OUT);
		Set<TermGraph> verbSubGraphs = new TermGraphBuilder().readFromCSV(VERB_OUT);
		UserActivityClusters activityClusters = new UserActivityClusters(verbSubGraphs, nounGraphs);

		System.out.println(new Date() + " " + "start reading activities");
		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/activities.graph")));

		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(new Date() + " " + "start add activity");
			String verb = null, noun = null, location = null, timeOfDay = null, avgDur = null;
			StringTokenizer tok = new StringTokenizer(line, ",");
			verb = tok.nextToken();
			noun = tok.nextToken();
			location = tok.nextToken();
			timeOfDay = tok.nextToken();
			avgDur = tok.nextToken();
			UserActivity act = new UserActivity(verb, noun, location, timeOfDay, avgDur);
			activityClusters.addActivity(act);
			System.out.println(new Date() + " " + "end add activity");
			// System.out.println(new Date() + " " + activityGraph.toString());
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