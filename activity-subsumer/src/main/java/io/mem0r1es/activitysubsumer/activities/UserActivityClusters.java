package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.utils.Utils;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	public Set<UserActivity> subsume(Set<UserActivity> activities) {
		Set<UserActivity> result = new HashSet<UserActivity>();
		long bestVerbCluserID = -1;
		long bestNounCluserID = -1;
		double bestOverallScore = 0;

		Set<String> mandatoryNouns = new HashSet<String>();
		for (UserActivity activity : activities) {
			mandatoryNouns.add(activity.getNoun());
		}

		for (Entry<Long, Set<UserActivity>> entry : activityClusters.entrySet()) {
			if (entry.getValue().containsAll(activities)) {
				long cluserID = entry.getKey();

				Set<String> nounsInCluser = new HashSet<String>();
				for (UserActivity activity : entry.getValue()) {
					nounsInCluser.add(activity.getNoun());
				}

				double bestScore = 0;
				long bestNounCluser = -1;
				for (TermGraph nounGraph : nounGraphs) {
					Set<String> mandatoryNounsTemp = new HashSet<String>();
					mandatoryNounsTemp.addAll(mandatoryNouns);

					double count = 0;
					Set<String> words = nounGraph.getWords();

					for (String nounInCluser : nounsInCluser) {
						for (String word : words) {
							if (nounInCluser.equals(Utils.wordName(word))) {
								if (mandatoryNounsTemp.contains(nounInCluser)) {
									mandatoryNounsTemp.remove(nounInCluser);
								}
								count++;
							}
						}
					}

					double score = mandatoryNounsTemp.isEmpty() ? count / words.size() : 0;
					if (score > bestScore) {
						bestScore = score;
						bestNounCluser = nounGraph.getID();
					}
				}

				if (bestOverallScore < bestScore) {
					bestOverallScore = bestScore;
					bestVerbCluserID = cluserID;
					bestNounCluserID = bestNounCluser;
				}
			}
		}

		if (bestVerbCluserID == -1 || bestNounCluserID == -1) {
			return result;
		}

		TermGraph bestVerbCluser = null;
		TermGraph bestNounCluser = null;
		for (TermGraph graph : verbGraphs) {
			if (graph.getID() == bestVerbCluserID) {
				bestVerbCluser = graph;
			}
		}
		for (TermGraph graph : nounGraphs) {
			if (graph.getID() == bestNounCluserID) {
				bestNounCluser = graph;
			}
		}

		Set<WordNetNode> destinationVerbs = new HashSet<WordNetNode>();
		Set<WordNetNode> destinationNouns = new HashSet<WordNetNode>();
		for (WordNetNode node : bestVerbCluser.vertexSet()) {
			for (String word : node.getWords()) {
				for (UserActivity activity : activities) {
					if (activity.getVerb().equals(Utils.wordName(word))) {
						destinationVerbs.add(node);
					}
				}
			}
		}
		for (WordNetNode node : bestNounCluser.vertexSet()) {
			for (String word : node.getWords()) {
				for (UserActivity activity : activities) {
					if (activity.getNoun().equals(Utils.wordName(word))) {
						destinationNouns.add(node);
					}
				}
			}
		}

		PathBuilder<WordNetNode, DefaultEdge> verbPaths = new PathBuilder<WordNetNode, DefaultEdge>(bestVerbCluser, bestVerbCluser.getRoot(), destinationVerbs);
		PathBuilder<WordNetNode, DefaultEdge> nounPaths = new PathBuilder<WordNetNode, DefaultEdge>(bestNounCluser, bestNounCluser.getRoot(), destinationNouns);

		for (WordNetNode verbLCA : verbPaths.getLCA(destinationVerbs)) {
			for (WordNetNode nounLCA : nounPaths.getLCA(destinationNouns)) {
				for (String verb : verbLCA.getWords()) {
					for (String noun : nounLCA.getWords()) {
						result.add(new UserActivity(verb, noun));
					}
				}
			}
		}
		return result;
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

		// Set<Map<Long>>

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