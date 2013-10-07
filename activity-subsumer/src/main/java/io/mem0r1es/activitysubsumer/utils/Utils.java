package io.mem0r1es.activitysubsumer.utils;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Utils {
    private Utils() {
    }

    public static String wordName(String word) {
        return word.split("\\.")[0];
    }

    public static Map<String, List<String>> buildSenseMap(DirectedAcyclicGraph<String, DefaultEdge> graph) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        for (String vertex : graph.vertexSet()) {
            String wordName = vertex.split("\\.")[0];

            List<String> senses;
            if (map.containsKey(wordName)) {
                senses = map.get(wordName);
            } else {
                senses = new ArrayList<String>();
            }
            senses.add(vertex);
            map.put(wordName, senses);
        }

        return map;
    }
}
