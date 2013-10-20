package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.utils.Utils;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

/**
 * Handles the {@link UserActivity} clusters and the noun/verb sub-graphs. Maintains a mapping
 * between the sub-graphs and the corresponding activities.
 * 
 * @author horiaradu
 */
public class UserActivityClusters {
	private Set<TermGraph> verbGraphs;
	private Set<TermGraph> nounGraphs;

	private Map<Long, Set<UserActivity>> activityClusters = new HashMap<Long, Set<UserActivity>>();

	public UserActivityClusters(Set<TermGraph> verbGraphs, Set<TermGraph> nounGraphs) {
		this.verbGraphs = verbGraphs;
		this.nounGraphs = nounGraphs;
	}

	/**
	 * Add an activity and connect it to the appropriate cluster(s).
	 * 
	 * @param activity
	 */
	public void addActivity(UserActivity activity) {
		String verb = activity.getVerb();
		for (TermGraph graph : verbGraphs) {
			if (graph.containsNonSenseTerm(verb)) {
				Set<UserActivity> correspondingActivities = activityClusters.get(graph.getID());
				if (correspondingActivities == null) {
					correspondingActivities = new HashSet<UserActivity>();
					activityClusters.put(graph.getID(), correspondingActivities);
				}
				correspondingActivities.add(activity);
			}
		}
	}

	/**
	 * Find the set of activities which are "specializations" of the given general activity.
	 * 
	 * @param generalActivity
	 * @return
	 */
	public Set<UserActivity> findActivities(UserActivity generalActivity) {
		Set<UserActivity> result = new HashSet<UserActivity>();

		String generalVerb = generalActivity.getVerb();
		String generalNoun = generalActivity.getNoun();

		for (TermGraph graph : verbGraphs) {
			if (graph.containsNonSenseTerm(generalVerb)) {
				// the cluster has a sense for this verb
				long graphID = graph.getID();

				// set of nouns we want to try to reach
				Set<String> nouns = new HashSet<String>();
				Set<UserActivity> activitiesInCluster = activityClusters.get(graphID);
				for (UserActivity activity : activitiesInCluster) {
					nouns.add(activity.getNoun());
				}

				for (TermGraph nounSubGraph : nounGraphs) {
					Set<WordNetNode> foundSenses = nounSubGraph.getSensesForNonSenseTerm(generalNoun);

					// TEMP CODE
					Set<WordNetNode> destinations = new HashSet<WordNetNode>();
					for (String noun : nouns) {
						for (WordNetNode foundNounSenses : nounSubGraph.getSensesForNonSenseTerm(noun)) {
							destinations.add(foundNounSenses);
						}
					}
					// ********

					for (WordNetNode node : foundSenses) {
						PathBuilder<WordNetNode, DefaultEdge> pathBuilder = new PathBuilder<WordNetNode, DefaultEdge>(nounSubGraph, node, destinations);

						for (WordNetNode destination : destinations) {
							Set<List<WordNetNode>> pathsToDestination = pathBuilder.getPathsToDestination(destination);
							if (pathsToDestination != null && pathsToDestination.isEmpty() == false) {
								// we have reached this destination
								for (UserActivity activity : activitiesInCluster) {
									for (String word : destination.getWords()) {
										if (Utils.wordName(word).equals(activity.getNoun())) {
											result.add(activity);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public Map<Long, Set<UserActivity>> getActivityMapping() {
		return activityClusters;
	}

}