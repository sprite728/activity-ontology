package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.utils.TermGraphBuilder;
import io.mem0r1es.activitysubsumer.utils.Utils;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

/**
 * Helper class used to count the number of cycles and the number of occurences of different senses
 * of the same word in a single sub-graph
 * 
 * @author horia
 */
@Test
public class CycleCounter {
	private static final String NOUNS_GRAPH = "src/test/resources/nouns.graph";
	private static final String NOUN_SYNONIMS = "src/test/resources/noun_senses.txt";
	private static final String NOUN_ROOTS = "src/test/resources/noun_roots";
	private static final String NOUN_NODES_GEPHI = "out/nouns_nodes.csv";
	private static final String NOUN_EDGES_GEPHI = "out/nouns_edges.csv";
	private static final String NOUN_OUT = "out/nouns_out_syn_final.csv";

	private static final String VERBS_GRAPH = "src/test/resources/verbs.graph";
	private static final String VERB_SYNONIMS = "src/test/resources/verb_senses.txt";
	private static final String VERB_ROOTS = "src/test/resources/verb_roots";
	private static final String VERB_NODES_GEPHI = "out/verbs_nodes.csv";
	private static final String VERB_EDGES_GEPHI = "out/verbs_edges.csv";
	private static final String VERB_OUT = "out/verbs_out_syn_final.csv";

	@Test
	public void main() throws IOException {
		long startTime = System.currentTimeMillis();
		new TermGraphBuilder().buildFiles(NOUN_SYNONIMS, NOUN_ROOTS, NOUNS_GRAPH, NOUN_NODES_GEPHI, NOUN_EDGES_GEPHI, NOUN_OUT);
		new TermGraphBuilder().buildFiles(VERB_SYNONIMS, VERB_ROOTS, VERBS_GRAPH, VERB_NODES_GEPHI, VERB_EDGES_GEPHI, VERB_OUT);
		long stopTime = System.currentTimeMillis();
		Object elapsedTime = stopTime - startTime;
		System.out.println(new Date() + " " + "Add activities Time:" + elapsedTime);

		Set<TermGraph> nounGraphs = new TermGraphBuilder().readFromCSV(NOUN_OUT);
		Set<TermGraph> verbSubGraphs = new TermGraphBuilder().readFromCSV(VERB_OUT);

		for (TermGraph graph : nounGraphs) {
			Map<String, Integer> repeatMap = getRepeats(graph);
			if (repeatMap.isEmpty() == false) {
				System.out.println("noun graph " + graph.getID() + ", rooted in " + graph.getRoot() + ": " + repeatMap);
			}
		}

		for (TermGraph graph : verbSubGraphs) {
			Map<String, Integer> repeatMap = getRepeats(graph);
			if (repeatMap.isEmpty() == false) {
				System.out.println("verb graph " + graph.getID() + ", rooted in " + graph.getRoot() + ": " + repeatMap);
			}
		}

	}

	public Map<String, Integer> getRepeats(TermGraph graph) {
		Set<String> words = new HashSet<String>();
		Map<String, Integer> repeatMap = new HashMap<String, Integer>();

		for (WordNetNode node : graph.vertexSet()) {
			for (String wordWithSense : node.getWords()) {
				String word = Utils.wordName(wordWithSense);
				if (words.contains(word)) {
					Integer integer = repeatMap.get(word);
					if (integer == null) {
						integer = 2;
					} else {
						integer++;
					}
					repeatMap.put(word, integer);
				} else {
					words.add(word);
				}
			}
		}

		return repeatMap;
	}
}