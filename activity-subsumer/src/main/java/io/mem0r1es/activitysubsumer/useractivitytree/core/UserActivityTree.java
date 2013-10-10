package io.mem0r1es.activitysubsumer.useractivitytree.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

public class UserActivityTree {
	private final DirectedAcyclicGraph<UserActivity, DefaultEdge> graph = new DirectedAcyclicGraph<UserActivity, DefaultEdge>(DefaultEdge.class);

	public UserActivityTree() {
		graph.addVertex(UserActivity.DEFAULT_NODE);
	}

	public void add(UserActivity activity) {
		if (graph.vertexSet().contains(activity)) {
			return;
		}
		graph.addVertex(activity);
		graph.addEdge(UserActivity.DEFAULT_NODE, activity);
	}

	public Set<UserActivity> getNodes() {
		return graph.vertexSet();
	}

	public UserActivity getNode(String verb, String noun) {
		for (UserActivity activity : graph.vertexSet()) {
			if (activity.getVerb().equals(verb) && activity.getNoun().equals(noun)) {
				return activity;
			}
		}
		return null;
	}

	public void addChild(UserActivity activity, UserActivity targetActivity) {
		if (graph.containsVertex(targetActivity) == false) {
			throw new IllegalArgumentException();
		}
		if (graph.containsVertex(activity)) {
			throw new IllegalArgumentException();
		}

		graph.addVertex(activity);
		graph.addEdge(targetActivity, activity);
	}

	public void insertAbove(UserActivity activity, UserActivity targetActivity) {
		if (graph.containsVertex(targetActivity) == false) {
			throw new IllegalArgumentException();
		}
		if (graph.containsVertex(activity)) {
			throw new IllegalArgumentException();
		}

		graph.addVertex(activity);
		for (DefaultEdge edge : graph.edgesOf(targetActivity)) {
			UserActivity edgeTarget = graph.getEdgeTarget(edge);
			UserActivity edgeSource = graph.getEdgeSource(edge);
			if (edgeTarget.equals(targetActivity)) {
				graph.removeEdge(edge);
				graph.addEdge(edgeSource, activity);
			}
		}
		graph.addEdge(activity, targetActivity);
	}

	public Map<UserActivity, Set<UserActivity>> getSubtreesByVerbs(Set<String> rootVerbs) {
		Map<UserActivity, Set<UserActivity>> result = new HashMap<UserActivity, Set<UserActivity>>();
		for (String rootVerb : rootVerbs) {
			for (UserActivity activity : graph.vertexSet()) {
				if (activity.getVerb().equals(rootVerb)) {
					result.put(activity, getSubtree(activity));
				}
			}
		}
		return result;
	}

	public Set<UserActivity> getSubtree(UserActivity root) {
		Set<UserActivity> result = new HashSet<UserActivity>();
		BreadthFirstIterator<UserActivity, DefaultEdge> iterator = new BreadthFirstIterator<UserActivity, DefaultEdge>(graph, root);
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}
	
	@Override
	public String toString() {
		return graph.edgeSet().toString();
	}
}