package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.GraphUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Singleton which wraps the WordNet nouns and verbs graphs. Also, it maintains a map from each noun
 * or verb to all the available senses for that noun or verb.
 * 
 * @author Horia Radu
 */
public class WordNetGraphs {
	private final DirectedAcyclicGraph<String, DefaultEdge> nouns;
	private final DirectedAcyclicGraph<String, DefaultEdge> verbs;

	// Mappings between words and word senses
	private final Map<String, Set<String>> senseMapVerbs;
	private final Map<String, Set<String>> senseMapNouns;

	private WordNetGraphs(InputStream nounIS, InputStream verbIS) throws IOException {
		nouns = GraphUtils.buildDAG(nounIS, DefaultEdge.class);
		verbs = GraphUtils.buildDAG(verbIS, DefaultEdge.class);

		senseMapVerbs = WordNetUtils.buildSenseMap(verbs);
		senseMapNouns = WordNetUtils.buildSenseMap(nouns);
	}

	private static WordNetGraphs INSTANCE;

	public static final WordNetGraphs instance() {
		return INSTANCE;
	}

	public static final WordNetGraphs initialize(InputStream nounIS, InputStream verbIS) {
		try {
			INSTANCE = new WordNetGraphs(nounIS, verbIS);
		} catch (IOException e) {
			e.printStackTrace();
			INSTANCE = null;
		}
		return INSTANCE;
	}

	public DirectedAcyclicGraph<String, DefaultEdge> getNouns() {
		return nouns;
	}

	public DirectedAcyclicGraph<String, DefaultEdge> getVerbs() {
		return verbs;
	}

	public Map<String, Set<String>> getSenseMapVerbs() {
		return senseMapVerbs;
	}

	public Map<String, Set<String>> getSenseMapNouns() {
		return senseMapNouns;
	}

}
