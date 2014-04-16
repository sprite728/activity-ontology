package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TermGraphBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;
import io.mem0r1es.activitysubsumer.wordnet.WordNetUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

/**
 * Helper class used to count the number of cycles and the number of occurences of different senses
 * of the same word in a single sub-graph
 * 
 * @author horia
 */
public class CycleCounter {


	@Test
	public void main() throws IOException {
		long startTime = System.currentTimeMillis();
		new TermGraphBuilder().buildFiles(Cons.NOUN_SYNONYMS, Cons.NOUN_ROOTS, Cons.NOUNS_GRAPH, Cons.NOUN_NODES_GEPHI, Cons.NOUN_EDGES_GEPHI, Cons.NOUN_OUT);
		new TermGraphBuilder().buildFiles(Cons.VERB_SYNONYMS, Cons.VERB_ROOTS, Cons.VERBS_GRAPH, Cons.VERB_NODES_GEPHI, Cons.VERB_EDGES_GEPHI, Cons.VERB_OUT);
		long stopTime = System.currentTimeMillis();
		Object elapsedTime = stopTime - startTime;
		System.out.println(new Date() + " " + "Add activities Time:" + elapsedTime);

		Set<TermGraph> nounGraphs = new TermGraphBuilder().readFromCSV(Cons.NOUN_OUT);
		Set<TermGraph> verbSubGraphs = new TermGraphBuilder().readFromCSV(Cons.VERB_OUT);

		for (TermGraph graph : nounGraphs) {
			Map<String, Integer> repeatMap = getRepeats(graph);
			if (!repeatMap.isEmpty()) {
				System.out.println("noun graph " + graph.getID() + ", rooted in " + graph.getRoot() + ": " + repeatMap);
			}
		}

		for (TermGraph graph : verbSubGraphs) {
			Map<String, Integer> repeatMap = getRepeats(graph);
			if (!repeatMap.isEmpty()) {
				System.out.println("verb graph " + graph.getID() + ", rooted in " + graph.getRoot() + ": " + repeatMap);
			}
		}

	}

	public Map<String, Integer> getRepeats(TermGraph graph) {
		Set<String> words = new HashSet<String>();
		Map<String, Integer> repeatMap = new HashMap<String, Integer>();

		for (WordNetNode node : graph.vertexSet()) {
			for (String wordWithSense : node.getWords()) {
				String word = WordNetUtils.wordName(wordWithSense);
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