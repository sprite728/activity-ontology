package io.mem0r1es.activitysubsumer.utils;

import io.mem0r1es.activitysubsumer.useractivitytree.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

public class TermGraphBuilder {
	public static int NR_CYCLES = 0;
	public static int NR_SELF_CYCLES = 0;

	private static Map<String, Set<String>> synonyms;
	private static Set<TermGraph> graphs;
	private static DirectedAcyclicGraph<String, DefaultEdge> wordDag;

	public Set<TermGraph> readFromCSV(String inCSV) throws IOException {
		Set<TermGraph> result = new HashSet<TermGraph>();

		TermGraph current = null;
		BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(inCSV)));
		for (String line = buf.readLine(); line != null; line = buf.readLine()) {
			if (line.startsWith("#"))
				continue;

			if (line.startsWith("!root")) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				tokenizer.nextToken();
				String key = tokenizer.nextToken();
				current = new TermGraph(vertexFromCSV(key));
				result.add(current);
			} else {
				String[] tokens = line.split(";");
				WordNetNode source = vertexFromCSV(tokens[1]);
				WordNetNode target = vertexFromCSV(tokens[2]);
				current.addVertex(source);
				current.addVertex(target);
				try {
					current.addEdge(source, target);
				} catch (Exception e) {
					System.out.println();
				}
			}
		}
		buf.close();

		return result;
	}

	public void buildFiles(String synonymsFile, String rootsFile, String dagFile, String termsCSV, String edgesCSV, String outCSV) throws IOException {
		System.out.println("start reading synonyms");
		synonyms = new HashMap<String, Set<String>>();
		BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(synonymsFile)));
		for (String line = buf.readLine(); line != null; line = buf.readLine()) {
			if (line.startsWith("#"))
				continue;

			StringTokenizer tokenizer = new StringTokenizer(line);
			String key = tokenizer.nextToken();

			Set<String> syns = new HashSet<String>();
			while (tokenizer.hasMoreTokens()) {
				syns.add(tokenizer.nextToken());
			}
			synonyms.put(key, syns);
		}
		buf.close();
		System.out.println("end reading synonyms");

		System.out.println("start reading sub graph roots");
		graphs = new HashSet<TermGraph>();
		buf = new BufferedReader(new InputStreamReader(new FileInputStream(rootsFile)));
		for (String line = buf.readLine(); line != null; line = buf.readLine()) {
			if (line.startsWith("#"))
				continue;

			StringTokenizer tokenizer = new StringTokenizer(line);
			String key = tokenizer.nextToken();

			for (Set<String> words : getSynonims(key)) {
				WordNetNode root = new WordNetNode(words);
				TermGraph termGraph = new TermGraph(root);
				graphs.add(termGraph);
			}
		}
		buf.close();
		System.out.println("end reading sub graph roots");

		System.out.println("start reading dag");
		buf = new BufferedReader(new InputStreamReader(new FileInputStream(dagFile)));
		wordDag = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		for (String line = buf.readLine(); line != null; line = buf.readLine()) {
			if (line.startsWith("#"))
				continue;

			StringTokenizer tokenizer = new StringTokenizer(line);
			String node = tokenizer.nextToken();
			wordDag.addVertex(node);

			while (tokenizer.hasMoreTokens()) {
				String next = tokenizer.nextToken();
				wordDag.addVertex(next);
				wordDag.addEdge(node, next);
			}
		}
		buf.close();
		System.out.println("start reading dag");

		StringBuilder vertices = new StringBuilder("Id\n");
		StringBuilder edges = new StringBuilder("Source;Target\n");

		System.out.println("start building subgraphs");
		for (TermGraph graph : graphs) {
			System.out.println("start building subgraph " + graph.getID());
			for (String word : graph.getRoot().getWords()) {
				dfs(word, graph);
			}

			System.out.println("end building subgraph " + graph.getID());
			vertices.append("!" + graph.getID());
			vertices.append(vertexSetToCSV(graph));
			vertices.append("!!" + graph.getID());

			edges.append("!" + graph.getID());
			edges.append(edgeSetToCSV(graph));
			edges.append("!!" + graph.getID());
			System.out.println("end writing subgraph " + graph.getID());
		}
		System.out.println("end building subgraphs");

		StringBuilder outBuilder = new StringBuilder();
		for (TermGraph graph : graphs) {
			outBuilder.append("!root ");
			outBuilder.append(vertexToCSV(graph.getRoot()));
			outBuilder.append("\n");
			for (DefaultEdge edge : graph.edgeSet()) {
				outBuilder.append(graph.getID());
				outBuilder.append(";");
				outBuilder.append(vertexToCSV(graph.getEdgeSource(edge)));
				outBuilder.append(";");
				outBuilder.append(vertexToCSV(graph.getEdgeTarget(edge)));
				outBuilder.append("\n");
			}
		}

		FileWriter writer = new FileWriter(new File(termsCSV));
		writer.write(vertices.toString());
		writer.close();
		writer = new FileWriter(new File(edgesCSV));
		writer.write(edges.toString());
		writer.close();
		writer = new FileWriter(new File(outCSV));
		writer.write(outBuilder.toString());
		writer.close();

		System.out.println(NR_CYCLES);
		System.out.println(NR_SELF_CYCLES);
	}

	private Set<Set<String>> getSynonims(String key) {
		Set<Set<String>> words = new HashSet<Set<String>>();
		for (Set<String> syns : synonyms.values()) {
			if (syns.contains(key)) {
				words.add(syns);
			}
		}

		if (words.isEmpty()) {
			words.add(Collections.singleton(key));
		}
		return words;
	}

	private void dfs(String vertex, TermGraph termGraph) {
		Set<WordNetNode> nodes = termGraph.getNodes(vertex);
		for (WordNetNode node : nodes) {
			System.out.println("visiting" + node + " in graph " + termGraph.getID());
		}

		for (DefaultEdge edge : wordDag.edgesOf(vertex)) {
			String target = wordDag.getEdgeTarget(edge);
			String source = wordDag.getEdgeSource(edge);
			if (source.equals(vertex)) {
				// only look at forward edges
				Set<Set<String>> targetSynSets = getSynonims(target);
				for (Set<String> targetSyns : targetSynSets) {
					WordNetNode targetNode = new WordNetNode(targetSyns);
					System.out.println("add node " + targetNode + " to graph " + termGraph.getID());
					termGraph.addVertex(targetNode);

					for (WordNetNode node : nodes) {
						if (node.equals(targetNode) == false) {
							System.out.println("add edge " + node + " -> " + targetNode + " to graph " + termGraph.getID());
							try {
								termGraph.addEdge(node, targetNode);
							} catch (IllegalArgumentException e) {
								System.out.println("cycle: " + node + " -> " + targetNode);
								Set<List<WordNetNode>> pathsToDestination =
										new PathBuilder<WordNetNode, DefaultEdge>(termGraph, targetNode, Collections.singleton(node)).getPathsToDestination(node);
								for (List<WordNetNode> path : pathsToDestination) {
									System.out.println(path);
								}
								NR_CYCLES++;
							}
						}
					}

					dfs(target, termGraph);
				}
			}
		}
	}

	private String edgeSetToCSV(TermGraph graph) {
		StringBuilder result = new StringBuilder();
		for (DefaultEdge edge : graph.edgeSet()) {
			result.append(vertexToCSV(graph.getEdgeSource(edge)));
			result.append(";");
			result.append(vertexToCSV(graph.getEdgeTarget(edge)));
			result.append("\n");
		}
		return result.toString();
	}

	private String vertexSetToCSV(TermGraph graph) {
		StringBuilder result = new StringBuilder();
		for (WordNetNode node : graph.vertexSet()) {
			String line = "";
			for (String word : node.getWords()) {
				if (line.equals("") == false) {
					line += "|";
				}
				line += word;
			}
			result.append(vertexToCSV(node));
			result.append("\n");
		}
		return result.toString();
	}

	private String vertexToCSV(WordNetNode node) {
		StringBuilder line = new StringBuilder();
		for (String word : node.getWords()) {
			if (line.toString().equals("") == false) {
				line.append("|");
			}
			line.append(word);
		}
		return line.toString();
	}

	private WordNetNode vertexFromCSV(String key) {
		String[] items = key.split("\\|");
		return new WordNetNode(items);
	}
}