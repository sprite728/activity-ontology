package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.wordnet.TermGraph;
import io.mem0r1es.activitysubsumer.wordnet.WordNetNode;
import io.mem0r1es.activitysubsumer.wordnet.WordNetUtils;

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
 * @author Horia Radu
 */
public class UserActivityClusters {
	private static final String TAG = UserActivityClusters.class.getCanonicalName();

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
	 * Add the activity to the given set of clusters. To only be used when building the clusters
	 * from previously stored information.
	 * 
	 * @param activity
	 * @param clusterIDs
	 */
	public void addActivity(UserActivity activity, Set<Long> clusterIDs) {
		for (long clusterID : clusterIDs) {
			Set<UserActivity> correspondingActivities = activityClusters.get(clusterID);
			if (correspondingActivities == null) {
				correspondingActivities = new HashSet<UserActivity>();
				activityClusters.put(clusterID, correspondingActivities);
			}
			correspondingActivities.add(activity);
		}
	}

	/**
	 * Return the set of {@link UserActivity} which subsume the given set of activities.
	 * 
	 * @param activities
	 * @return
	 */
	public Set<UserActivity> subsume(Set<UserActivity> activities) {
		Set<UserActivity> result = new HashSet<UserActivity>();
		long bestVerbSubGraphID = -1;
		long bestNounSubGraphID = -1;
		double bestOverallScore = 0;

		// set of mandatory nouns which we must find in a noun sub-graph
		Set<String> mandatoryNouns = new HashSet<String>();
		for (UserActivity activity : activities) {
			mandatoryNouns.add(activity.getNoun());
		}

		// for each activity cluster (verb sub-graph)
		for (Entry<Long, Set<UserActivity>> entry : activityClusters.entrySet()) {
			if (entry.getValue().containsAll(activities)) {
				// if we find all the given activities
				long verbSubGraphID = entry.getKey();

				// set of nouns of activities in this cluster - see how many of these we find in a
				// noun sub-graph
				Set<String> nounsInVerbCluster = new HashSet<String>();
				for (UserActivity activity : entry.getValue()) {
					nounsInVerbCluster.add(activity.getNoun());
				}

				double bestScore = 0;
				long bestNounSubGraphIDForVerbSubGraph = -1;
				for (TermGraph nounGraph : nounGraphs) {
					// temporary set of the mandatory nouns
					Set<String> mandatoryNounsTemp = new HashSet<String>();
					mandatoryNounsTemp.addAll(mandatoryNouns);

					// count of how many nouns from the activity cluster we find in the noun
					// sub-graph
					double count = 0;

					Set<String> words = nounGraph.getWords();
					// all the noun senses in the sub-graph for the given nouns
					Set<WordNetNode> foundNounSenses = nounGraph.getSensesForNonSenseTerms(nounsInVerbCluster);
					for (WordNetNode node : foundNounSenses) {
						for (String word : node.getWords()) {
							String wordNoSense = WordNetUtils.wordName(word);
							// remove from mandatory nouns (if it's the case)
							mandatoryNounsTemp.remove(wordNoSense);
							if (nounsInVerbCluster.contains(wordNoSense)) {
								// if this is a sense of the nouns in the cluster
								count++;
							}
						}
					}

					// score is 0 if we didn't find all the mandatory nouns
					double score = mandatoryNounsTemp.isEmpty() ? count / words.size() : 0;
					// keep best noun sub-graph score
					if (score > bestScore) {
						bestScore = score;
						bestNounSubGraphIDForVerbSubGraph = nounGraph.getID();
					}
				}

				// keep the best verb sub-graph and the corresponding noun sub-graph
				if (bestOverallScore < bestScore) {
					bestOverallScore = bestScore;
					bestVerbSubGraphID = verbSubGraphID;
					bestNounSubGraphID = bestNounSubGraphIDForVerbSubGraph;
				}
			}
		}

		if (bestVerbSubGraphID == -1 || bestNounSubGraphID == -1) {
			return result;
		}

		TermGraph bestVernSubGraph = null;
		TermGraph bestNounSubGraph = null;
		for (TermGraph graph : verbGraphs) {
			if (graph.getID() == bestVerbSubGraphID) {
				bestVernSubGraph = graph;
			}
		}
		for (TermGraph graph : nounGraphs) {
			if (graph.getID() == bestNounSubGraphID) {
				bestNounSubGraph = graph;
			}
		}

		// get the set of destinations
		Set<String> mandatoryVerbs = new HashSet<String>();
		for (UserActivity activity : activities) {
			mandatoryVerbs.add(activity.getVerb());
		}
		Set<WordNetNode> destinationVerbs = bestVernSubGraph.getNodesForNonSenseTerms(mandatoryVerbs);
		Set<WordNetNode> destinationNouns = bestNounSubGraph.getNodesForNonSenseTerms(mandatoryNouns);

		// find all the paths to those destinations
		PathBuilder<WordNetNode, DefaultEdge> verbPaths = new PathBuilder<WordNetNode, DefaultEdge>(bestVernSubGraph, bestVernSubGraph.getRoot(), destinationVerbs);
		PathBuilder<WordNetNode, DefaultEdge> nounPaths = new PathBuilder<WordNetNode, DefaultEdge>(bestNounSubGraph, bestNounSubGraph.getRoot(), destinationNouns);

		// for each possible combination, build the activities
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

	public Map<Long, Set<UserActivity>> getActivityMapping() {
		return activityClusters;
	}

	/**
	 * Find the set of activities which are "specializations" of the given general activity.
	 * 
	 * @param generalActivity
	 * @return
	 */
	public Set<UserActivity> findActivities(UserActivity generalActivity) {
		System.out.println(TAG + "start findActivities()");
		Set<UserActivity> result = new HashSet<UserActivity>();

		String generalVerb = generalActivity.getVerb();
		String generalNoun = generalActivity.getNoun();

		Map<Long, Set<WordNetNode>> nounSubGraphIDToSensesMap = new HashMap<Long, Set<WordNetNode>>();
		for (TermGraph nounSubGraph : nounGraphs) {
			Set<WordNetNode> foundSenses = nounSubGraph.getSensesForNonSenseTerm(generalNoun);
			nounSubGraphIDToSensesMap.put(nounSubGraph.getID(), foundSenses);
		}

		for (TermGraph graph : verbGraphs) {
			if (graph.containsNonSenseTerm(generalVerb)) {
				result.addAll(UserActivityClusters.findActivitiesInVerbGraph(graph.getID(), nounGraphs, activityClusters.get(graph.getID()), nounSubGraphIDToSensesMap));
			}
		}
		return result;
	}

	/**
	 * Helper method which returns activities which are more specific than a given general activity.
	 * This method will be executed by an AsyncTask.
	 * 
	 * @param verbGraphID
	 *            - the id verb graph relative to which we are searching
	 * @param nounGraphs
	 *            - the set of noun graphs
	 * @param activitiesInCluster
	 *            - the set of activities in the verb graph. (candidate activities to be returned)
	 * @param nounSubGraphIDToSensesMap
	 *            - map of senses of the noun of a general activity
	 * @return - The set of activities which are reachable, based on the algorithm and the given
	 *         senses map.
	 */
	protected static final Set<UserActivity> findActivitiesInVerbGraph(long verbGraphID, Set<TermGraph> nounGraphs, Set<UserActivity> activitiesInCluster,
			Map<Long, Set<WordNetNode>> nounSubGraphIDToSensesMap) {
		System.out.println(TAG + "start findActivitiesInVerbGraph()");
		Set<UserActivity> result = new HashSet<UserActivity>();

		System.out.println(TAG + "start verb sub-graph" + verbGraphID);
		// set of nouns we want to try to reach
		Set<String> nouns = new HashSet<String>();
		for (UserActivity activity : activitiesInCluster) {
			nouns.add(activity.getNoun());
		}

		for (TermGraph nounSubGraph : nounGraphs) {
			System.out.println(TAG + "start noun sub-graph" + nounSubGraph.getID());
			Set<WordNetNode> foundSenses = nounSubGraphIDToSensesMap.get(nounSubGraph.getID());
			System.out.println(TAG + "found senses in noun sub-graph");

			Set<WordNetNode> destinations = nounSubGraph.getSensesForNonSenseTerms(nouns);
			System.out.println(TAG + "found destinations noun sub-graph");

			for (WordNetNode node : foundSenses) {
				System.out.println(TAG + "start bfs in noun sub-graph");
				PathBuilder<WordNetNode, DefaultEdge> pathBuilder = new PathBuilder<WordNetNode, DefaultEdge>(nounSubGraph, node, destinations);
				System.out.println(TAG + "end bfs in noun sub-graph");

				for (WordNetNode destination : destinations) {
					Set<List<WordNetNode>> pathsToDestination = pathBuilder.getPathsToDestination(destination);
					if (pathsToDestination != null && pathsToDestination.isEmpty() == false) {
						// we have reached this destination
						for (UserActivity activity : activitiesInCluster) {
							for (String word : destination.getWords()) {
								if (WordNetUtils.wordName(word).equals(activity.getNoun())) {
									result.add(activity);
								}
							}
						}
					}
				}
			}
			System.out.println(TAG + "end noun sub-graph" + nounSubGraph.getID());
		}
		System.out.println(TAG + "end verb sub-graph" + verbGraphID);
		return result;
	}
}
