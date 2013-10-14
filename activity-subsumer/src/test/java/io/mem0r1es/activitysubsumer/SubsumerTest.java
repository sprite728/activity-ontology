package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.Subsumer;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;
import io.mem0r1es.activitysubsumer.useractivitytree.core.WordNetGraphs;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.CSVExporter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.StringTokenizer;

import org.testng.annotations.Test;

/**
 * Created by George on 9/26/13.
 */
@Test
public class SubsumerTest {
	@Test
	public void testJavaSerialization() throws Exception {
		long startTime = System.currentTimeMillis();
		WordNetGraphs.initialize(new FileInputStream("src/test/resources/nouns.graph"), new FileInputStream("src/test/resources/verbs.graph"));
		Subsumer subsumer = new Subsumer();
		UserActivityGraph activityGraph = new UserActivityGraph();

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(new Date() + " " + new Date() + " " + "Create Subsumer Time:" + elapsedTime);
		startTime = System.currentTimeMillis();
		BufferedReader br;
		try {
			System.out.println(new Date() + " " + "start reading activities");
			br = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/activities.graph")));

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
				try {
					activityGraph.insertActivity(act, subsumer);
				} catch (IllegalArgumentException e) {
					if ("Unknown verb or noun".equals(e.getMessage()) == false) {
						e.printStackTrace();
					}
				}
				System.out.println(new Date() + " " + "end add activity");
				// System.out.println(new Date() + " " + activityGraph.toString());
			}
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println(new Date() + " " + "Add activities Time:" + elapsedTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		new CSVExporter().exportCSV("out_activity_graph_v0", activityGraph);
		System.out.println(activityGraph.NR_CYCLES);

		// getContext().deleteFile(FILENAME);
	}
}
