package io.mem0r1es.activitysubsumer.useractivitytree.core;

import io.mem0r1es.activitysubsumer.useractivitytree.algs.BreadthFirstShortestPath;
import io.mem0r1es.activitysubsumer.useractivitytree.algs.LowestCommonAncestor;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.GraphUtils;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.Pair;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>
 * Subsumer class to perform the subsumption/aggregation presented here:
 * https://github.com/MEM0R1ES/ontology/wiki/Subsumer
 * </p>
 * <p>
 * A reference Python implementation can be found here: https://github.com/MEM0R1ES
 * /ontology/blob/master/crowdsourcing/scripts/etc/subsumer .py
 * </p>
 * 
 * @author Sebastian Claici
 */
public final class Subsumer implements Serializable {
	private static final long serialVersionUID = 0L;

	private final DirectedAcyclicGraph<String, DefaultEdge> nouns;
	private final DirectedAcyclicGraph<String, DefaultEdge> verbs;

	private UserActivityTree userActivityTree;

	// Classes to perform lowest common ancestor search
	private final LowestCommonAncestor<String, DefaultEdge> verbsLCA;
	private final LowestCommonAncestor<String, DefaultEdge> nounsLCA;
	private final Map<String, List<UserActivity>> locationToActivityMapping;

	// Mappings between words and word senses
	private final Map<String, Set<String>> senseMapVerbs;
	private final Map<String, Set<String>> senseMapNouns;

	// Precomputed mappings between senses and maximum depth at which they
	// appear
	private final Map<String, Integer> depthMapNouns;
	private final Map<String, Integer> depthMapVerbs;

	public Subsumer(InputStream nounIS, InputStream verbIS) throws IOException {
		nouns = GraphUtils.buildDAG(nounIS, DefaultEdge.class);
		verbs = GraphUtils.buildDAG(verbIS, DefaultEdge.class);

		verbsLCA = new LowestCommonAncestor<String, DefaultEdge>(verbs);
		nounsLCA = new LowestCommonAncestor<String, DefaultEdge>(nouns);

		senseMapVerbs = Utils.buildSenseMap(verbs);
		senseMapNouns = Utils.buildSenseMap(nouns);

		userActivityTree = new UserActivityTree();
		locationToActivityMapping = new HashMap<String, List<UserActivity>>();

		depthMapNouns = GraphUtils.bfsExplore(nouns);
		depthMapVerbs = GraphUtils.bfsExplore(verbs);
	}

	public void test() {
		Set<DefaultEdge> edges = nouns.edgesOf("meal.n.01");
		System.out.println(nouns.containsEdge("meal.n.01", "lunch.n.01"));
	}

	/**
	 * <p>
	 * Add an activity to the subsumer.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             if the noun and/or verb are not in the word net list
	 */
	public void addActivity(String verb, String noun) {
		// convert terms to word net form
		verb = verb.toLowerCase().replace(' ', '_');
		noun = noun.toLowerCase().replace(' ', '_');

		if (!senseMapVerbs.containsKey(verb) || !senseMapNouns.containsKey(noun))
			throw new IllegalArgumentException("Unknown verb or noun");

		addActivity(new UserActivity(verb, noun));
	}

	public void addActivity(UserActivity activity) {
		// add/update location to activity mapping

		String noun = activity.getNoun();
		String verb = activity.getVerb();
		Set<String> verbSenses = senseMapVerbs.get(verb);
		Set<String> nounSenses = senseMapNouns.get(noun);

		if (verbSenses == null || nounSenses == null) {
			return;
		}

		// pairs of matched activities to parent activities
		Map<Pair<String, String>, Set<UserActivity>> parentToSiblingsMapping = new HashMap<Pair<String, String>, Set<UserActivity>>();

		// distance cache
		Map<String, BreadthFirstShortestPath<String, DefaultEdge>> nounsDistanceCache = new HashMap<String, BreadthFirstShortestPath<String, DefaultEdge>>();
		Map<String, BreadthFirstShortestPath<String, DefaultEdge>> verbsDistanceCache = new HashMap<String, BreadthFirstShortestPath<String, DefaultEdge>>();

		// Go through every pair of (noun, verb) senses, and try to find a match
		// already in the tree
		for (String nounSense : nounSenses) {
			for (String verbSense : verbSenses) {
				Pair<UserActivity, Pair<String, String>> subsumptionResult = subsume(verbSense, nounSense, nounsDistanceCache, verbsDistanceCache);
				if (subsumptionResult == null)
					continue;

				Set<UserActivity> siblings = parentToSiblingsMapping.get(subsumptionResult.second);
				if (siblings == null) {
					siblings = new HashSet<UserActivity>();
					parentToSiblingsMapping.put(subsumptionResult.second, siblings);
				}
				siblings.add(subsumptionResult.first);
			}
		}

		findBestMatch(activity, parentToSiblingsMapping);

		addLocationToActivityMapping(activity);
	}

	/**
	 * <p>
	 * We want to connect an {@link UserActivity} to its parent. Since we do not know which other
	 * activity is semantically closest to it, we need to find it.
	 * </p>
	 * <p>
	 * The algorithm works as follows:
	 * </p>
	 * <ol>
	 * <li>iterate through each existing activity</li>
	 * <li>find the semantic distance between them as a function of the distances between the verbs,
	 * and the distance between the nouns</li>
	 * <li>link the found activity with the given activity</li>
	 * </ol>
	 * 
	 * @return a Pair of best matching activity and
	 */
	private Pair<UserActivity, Pair<String, String>> subsume(String verbSense, String nounSense, Map<String, BreadthFirstShortestPath<String, DefaultEdge>> nounsDistanceCache,
			Map<String, BreadthFirstShortestPath<String, DefaultEdge>> verbsDistanceCache) {

		double bestMatchingScore = Double.POSITIVE_INFINITY;
		UserActivity bestMatchingActivity = null;
		String bestMatchingNounSense = null;
		String bestMatchingVerbSense = null;

		// TODO from boss Michele: first try to find for Verb; if cost is max value, then stop

		BreadthFirstShortestPath<String, DefaultEdge> nounShortestPaths = getBreadthFirstShortestPath(nouns, nounSense, nounsDistanceCache);
		BreadthFirstShortestPath<String, DefaultEdge> verbShortestPaths = getBreadthFirstShortestPath(verbs, verbSense, verbsDistanceCache);

		for (UserActivity other : userActivityTree.getNodes()) {
			if (other.equals(UserActivity.DEFAULT_NODE)) {
				continue;
			}
			String nounOther = other.getNoun();
			String verbOther = other.getVerb();

			// build the cache of distances for the whole graph
			Set<String> senseMappedNouns = senseMapNouns.get(nounOther);
			Set<String> senseMappedVerbs = senseMapVerbs.get(verbOther);
			if (senseMappedNouns == null || senseMappedVerbs == null) {
				continue;
			}
			for (String nounSenseOther : senseMappedNouns) {
				getBreadthFirstShortestPath(nouns, nounSenseOther, nounsDistanceCache);
			}

			for (String verbSenseOther : senseMappedVerbs) {
				getBreadthFirstShortestPath(verbs, verbSenseOther, verbsDistanceCache);
			}

			// Find an already existing activity which is closest to the (nounSense, verbSense)
			// activity
			for (String nounSenseOther : senseMappedNouns) {
				for (String verbSenseOther : senseMappedVerbs) {
					if (Utils.wordName(nounSenseOther).equals(Utils.wordName(nounSense)) && Utils.wordName(verbSenseOther).equals(Utils.wordName(verbSense)))
						continue;

					int costNouns = 0;
					int costVerbs = 0;
					costNouns = nounShortestPaths.getCost(nounSenseOther);
					costVerbs = verbShortestPaths.getCost(verbSenseOther);

					// no path from given noun to other activity noun, then try the other direction
					if (costNouns == Integer.MAX_VALUE) {
						BreadthFirstShortestPath<String, DefaultEdge> nounSenseOtherSP = getBreadthFirstShortestPath(nouns, nounSenseOther, nounsDistanceCache);
						costNouns = nounSenseOtherSP.getCost(nounSense);
					}
					// no path from given verb to other activity verb, then try the other direction
					if (costVerbs == Integer.MAX_VALUE) {
						BreadthFirstShortestPath<String, DefaultEdge> verbSenseOtherSP = getBreadthFirstShortestPath(verbs, verbSenseOther, verbsDistanceCache);
						costVerbs = verbSenseOtherSP.getCost(verbSense);
					}

					if (costNouns != Integer.MAX_VALUE) {
						System.out.println();
					}

					if (costVerbs != Integer.MAX_VALUE && costNouns != Integer.MAX_VALUE) {
						// Verbs are more important, thus are weighted more
						double score = (costVerbs * 2.0 + costNouns * 1.0) / 3.0;

						if (score < bestMatchingScore) {
							bestMatchingScore = score;
							bestMatchingActivity = other;
							bestMatchingNounSense = nounSenseOther;
							bestMatchingVerbSense = verbSenseOther;
						}
					}
				}
			}
		}

		if (bestMatchingActivity == null)
			return null;

		Pair<String, String> parentActivity = subsume(verbSense, nounSense, bestMatchingNounSense, bestMatchingVerbSense, nounsDistanceCache, verbsDistanceCache);
		return new Pair<UserActivity, Pair<String, String>>(bestMatchingActivity, parentActivity);
	}

	/**
	 * Find an activity that subsumes <b>act1</b> and <b>act2</b>
	 */
	private Pair<String, String> subsume(String verbSense, String nounSense, String nounSenseOther, String verbSenseOther,
			Map<String, BreadthFirstShortestPath<String, DefaultEdge>> nounsDistanceCache, Map<String, BreadthFirstShortestPath<String, DefaultEdge>> verbsDistanceCache) {
		String nounLCA, verbLCA;
		if (nounSense.equals(nounSenseOther)) {
			nounLCA = nounSense;
		} else {
			Set<String> nounsLCASet = nounsLCA.onlineLCA(nounSense, nounSenseOther);
			if (nounsLCASet.isEmpty())
				return null;

			if (nounsLCASet.contains(nounSense)) {
				nounLCA = nounSense;
			} else if (nounsLCASet.contains(nounSenseOther)) {
				nounLCA = nounSenseOther;
			} else {
				nounLCA = GraphUtils.findDeepest(nounsLCASet, nouns);
			}
		}

		if (verbSense.equals(verbSenseOther)) {
			verbLCA = verbSense;
		} else {
			Set<String> verbsLCASet = verbsLCA.onlineLCA(verbSense, verbSenseOther);
			if (verbsLCASet.isEmpty())
				return null;

			if (verbsLCASet.contains(verbSense)) {
				verbLCA = verbSense;
			} else if (verbsLCASet.contains(verbSenseOther)) {
				verbLCA = verbSenseOther;
			} else {
				verbLCA = GraphUtils.findDeepest(verbsLCASet, verbs);
			}
		}

		return new Pair<String, String>(verbLCA, nounLCA);
	}

	/**
	 * <p>
	 * Finds the activity that is the best match to the activity presented in addActivity.
	 * </p>
	 * <p/>
	 * <p>
	 * We define best match in this case to be the activity that is at the greatest depth in the
	 * verb and noun DAGs.
	 * </p>
	 */
	private void findBestMatch(UserActivity activity, Map<Pair<String, String>, Set<UserActivity>> parentToSiblingsMapping) {
		if (parentToSiblingsMapping.isEmpty()) {
			userActivityTree.add(activity);
			return;
		}

		double bestDepth = Double.NEGATIVE_INFINITY;

		// Find the parent activity which is deepest in the WordNet hierarchy
		Map.Entry<Pair<String, String>, Set<UserActivity>> bestEntry = null;
		for (Map.Entry<Pair<String, String>, Set<UserActivity>> entry : parentToSiblingsMapping.entrySet()) {
			String subsumedVerb = entry.getKey().first;
			String subsumedNoun = entry.getKey().second;

			int depthNoun = depthMapNouns.get(subsumedNoun);
			int depthVerb = depthMapVerbs.get(subsumedVerb);
			double depth = Math.sqrt(depthNoun * depthNoun + depthVerb * depthVerb);

			if (depth > bestDepth) {
				bestDepth = depth;
				bestEntry = entry;
			}
		}

		rebuildTree(activity, bestEntry.getKey(), bestEntry.getValue());
	}

	private UserActivity rebuildTree(UserActivity activity, Pair<String, String> bestParentActivity, Set<UserActivity> bestMatchedActivities) {
		/*
		 * Case 1: the bestParentActivity is not in the tree; activity and bestParentActivity must
		 * be added to the tree and activity + bestMatchedActivities must become children of
		 * bestParentActivity. bestParentActivity must be a child of all the parents of
		 * bestMatchedActivities attention: it is possible that bestParentActivity is the same as
		 * one of the bestMatchedActivities. Case 2: bestParentActivity is already in the tree. In
		 * this case, bestMatchedActivities are ignored since they must already be children of
		 * bestParentActivity (because all additions are with subsumtion and subsumtion works.
		 */

		// Create a new parent activity
		String parentVerb = Utils.wordName(bestParentActivity.first);
		String parentNoun = Utils.wordName(bestParentActivity.second);

		UserActivity parentActivity = userActivityTree.getNode(parentVerb, parentNoun);
		if (parentActivity == null) {
			parentActivity = new UserActivity(parentVerb, parentNoun);
			userActivityTree.add(parentActivity);

			for (UserActivity bestMatchedActivity : bestMatchedActivities) {
				if (bestMatchedActivity.equals(parentActivity) == false) {
					userActivityTree.add(bestMatchedActivity, parentActivity);
				}
			}
		}

		// Add (parent -> activity) and (parent -> bestMatchedActivity) to the
		// activity tree
		if (activity.equals(parentActivity) == false) {
			userActivityTree.add(activity, parentActivity);
		}

		return parentActivity;
	}

	/**
	 * @return a ranked list of activities for a particular location type.
	 */
	public List<UserActivity> getActivitiesForLocation(String location) {
		if (!locationToActivityMapping.containsKey(location))
			return new ArrayList<UserActivity>();

		List<UserActivity> activities = locationToActivityMapping.get(location);
		Collections.sort(activities, new Comparator<UserActivity>() {
			public int compare(UserActivity act1, UserActivity act2) {
				if (act1.getScore() > act2.getScore())
					return 1;
				else if (act1.getScore() < act2.getScore())
					return -1;
				return 0;
			}
		});

		return activities;
	}

	private BreadthFirstShortestPath<String, DefaultEdge> getBreadthFirstShortestPath(DirectedAcyclicGraph<String, DefaultEdge> graph, String termWithSense,
			Map<String, BreadthFirstShortestPath<String, DefaultEdge>> distanceCache) {
		BreadthFirstShortestPath<String, DefaultEdge> result = distanceCache.get(termWithSense);
		if (result != null) {
			return result;
		}
		result = new BreadthFirstShortestPath<String, DefaultEdge>(graph, termWithSense);
		distanceCache.put(termWithSense, result);
		return result;
	}

	private void addLocationToActivityMapping(UserActivity act) {
		Set<String> locations = act.getLocations();
		for (String location : locations)
			addLocationToActivityMapping(act, location);
	}

	private void addLocationToActivityMapping(UserActivity act, String location) {
		List<UserActivity> activities;
		if (locationToActivityMapping.containsKey(location)) {
			activities = locationToActivityMapping.get(location);
		} else {
			activities = new ArrayList<UserActivity>();
		}
		activities.add(act);
		locationToActivityMapping.put(location, activities);
	}

	@Override
	public String toString() {
		return userActivityTree.getParentRelations().toString();
	}
}
