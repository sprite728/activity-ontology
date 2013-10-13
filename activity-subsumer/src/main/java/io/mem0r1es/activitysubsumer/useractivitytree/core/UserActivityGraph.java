package io.mem0r1es.activitysubsumer.useractivitytree.core;

import io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.Subsumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

/**
 * This class represents a data structure for holding all the UserActivities in a directed acyclic
 * graph. The graph has a <b>root node</b> which has no incoming edges; this node is
 * UserActivity.DEFAULT_NODE. In order to properly use the activity structure, you should check out
 * the {@link Subsumer}.
 * 
 * @author horiaradu
 */
public class UserActivityGraph {
	private final DirectedAcyclicGraph<UserActivity, DefaultEdge> graph = new DirectedAcyclicGraph<UserActivity, DefaultEdge>(DefaultEdge.class);

	public UserActivityGraph() {
		graph.addVertex(UserActivity.DEFAULT_NODE);
	}

	/**
	 * Add an activity to the graph. When the activity is added, if a similar activity is found
	 * existing in the graph, then a more general activity for the two is constructed and it is
	 * added as the <b>paren</b> of the two.
	 * 
	 * @param activity
	 * @param subsumer
	 */
	public void insertActivity(UserActivity activity, Subsumer subsumer) {
		if (graph.vertexSet().contains(activity)) {
			return;
		}
		subsumer.addActivity(activity, this);
	}

	/**
	 * @return the list of all nodes
	 */
	public Set<UserActivity> getNodes() {
		return graph.vertexSet();
	}

	/**
	 * @param verb
	 * @param noun
	 * @return if an activity with this verb and noun exists, then it is returned
	 */
	public UserActivity getNode(String verb, String noun) {
		for (UserActivity activity : graph.vertexSet()) {
			if (activity.getVerb().equals(verb) && activity.getNoun().equals(noun)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * Add this activity as a direct descendant of the UserActivity.DEFAULT_ROOT
	 * 
	 * @param activity
	 */
	public void add(UserActivity activity) {
		if (graph.vertexSet().contains(activity)) {
			return;
		}
		graph.addVertex(activity);
		graph.addEdge(UserActivity.DEFAULT_NODE, activity);
	}

	/**
	 * Adds activity as a child of targetActivity. If activity is already in the graph or if
	 * targetActivity isn't in the graph, then an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param activity
	 * @param targetActivity
	 * @throws IllegalArgumentException
	 */
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

	/**
	 * Insert activity as a <b>parent</b> of targetActivity. All the <b>old parents</b> of
	 * targetActivity will become parents of activity. If activity is already in the graph or if
	 * targetActivity isn't in the graph, then an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param activity
	 * @param targetActivity
	 * @throws IllegalArgumentException
	 */
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

	/**
	 * The method searches inside the graph for activities which have a verb present in the given
	 * verbSet. If an activity is found, then it is added inside the returned map as a key; the
	 * value represents the set of activities reachable from the found activity.
	 * 
	 * @param rootVerbs
	 * @return
	 */
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

	/**
	 * @param root
	 * @return the set of activities reachable from the given activity
	 */
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

	public Set<DefaultEdge> getEdges() {
		return graph.edgeSet();
	}
	
	public UserActivity getEdgeSource(DefaultEdge edge) {
		return graph.getEdgeSource(edge);
	}
	
	public UserActivity getEdgeTarget(DefaultEdge edge) {
		return graph.getEdgeTarget(edge);
	}
}