package io.mem0r1es.activitysubsumer.wordnet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

public final class WordNetUtils {
	private WordNetUtils() {
	}

	public static String wordName(String word) {
		return word.split("\\.")[0];
	}

	public static Map<String, Set<String>> buildSenseMap(DirectedAcyclicGraph<String, DefaultEdge> graph) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		for (String vertex : graph.vertexSet()) {
			String wordName = vertex.split("\\.")[0];

			Set<String> senses;
			if (map.containsKey(wordName)) {
				senses = map.get(wordName);
			} else {
				senses = new HashSet<String>();
			}
			senses.add(vertex);
			map.put(wordName, senses);
		}

		return map;
	}
}
