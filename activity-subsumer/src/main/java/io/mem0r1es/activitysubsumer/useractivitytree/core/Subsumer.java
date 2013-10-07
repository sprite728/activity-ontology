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
 * A reference Python implementation can be found here:
 * https://github.com/MEM0R1ES
 * /ontology/blob/master/crowdsourcing/scripts/etc/subsumer .py
 * </p>
 * 
 * @author Sebastian Claici
 */
public final class Subsumer implements Serializable {
	private static final long serialVersionUID = 0L;

	private final DirectedAcyclicGraph<String, DefaultEdge> nouns;
	private final DirectedAcyclicGraph<String, DefaultEdge> verbs;

	private final Set<UserActivity> activities;
	private final Map<UserActivity, List<UserActivity>> parentActivities;

	// Classes to perform lowest common ancestor search
	private final LowestCommonAncestor<String, DefaultEdge> verbsLCA;
	private final LowestCommonAncestor<String, DefaultEdge> nounsLCA;
	private final Map<String, List<UserActivity>> locationToActivityMapping;

	// Mappings between words and word senses
	private final Map<String, List<String>> senseMapVerbs;
	private final Map<String, List<String>> senseMapNouns;

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

		activities = new HashSet<UserActivity>();
		parentActivities = new HashMap<UserActivity, List<UserActivity>>();
		locationToActivityMapping = new HashMap<String, List<UserActivity>>();

		depthMapNouns = GraphUtils.bfsExplore(nouns);
		depthMapVerbs = GraphUtils.bfsExplore(verbs);
	}

	/**
	 * <p>
	 * Add an activity to the subsumer.
	 * </p>
	 */
	public void addActivity(String verb, String noun) {
		verb = verb.toLowerCase().replace(' ', '_');
		noun = noun.toLowerCase().replace(' ', '_');
		if (!senseMapVerbs.containsKey(verb)
				|| !senseMapNouns.containsKey(noun))
			throw new IllegalArgumentException("Unknown verb or noun");

		addActivity(new UserActivity(verb, noun));
	}

	public void addActivity(UserActivity activity) {
		addLocationToActivityMapping(activity);
		activities.add(activity);
		if (!parentActivities.containsKey(activity))
			parentActivities.put(activity, new ArrayList<UserActivity>());

		String noun = activity.getNoun();
		String verb = activity.getVerb();
		List<String> verbSenses = senseMapVerbs.get(verb);
		List<String> nounSenses = senseMapNouns.get(noun);

		if (verbSenses == null || nounSenses == null)
			return;

		List<Pair<String, String>> parentActivities = new ArrayList<Pair<String, String>>();
		List<UserActivity> matchedActivities = new ArrayList<UserActivity>();

		// Go through every pair of (noun, verb) senses, and try to find a match
		// already in the tree
		for (String nounSense : nounSenses) {
			for (String verbSense : verbSenses) {
				Pair<UserActivity, Pair<String, String>> subsumptionResult = subsume(
						verbSense, nounSense);
				if (subsumptionResult == null)
					continue;

				parentActivities.add(subsumptionResult.second);
				matchedActivities.add(subsumptionResult.first);
			}
		}

		findBestMatch(activity, matchedActivities, parentActivities);
	}

	/**
	 * <p>
	 * We want to connect an {@link UserActivity} to its parent. Since we do not
	 * know which other activity is semantically closest to it, we need to find
	 * it.
	 * </p>
	 * <p>
	 * The algorithm works as follows:
	 * </p>
	 * <ol>
	 * <li>iterate through each existing activity</li>
	 * <li>find the semantic distance between them as a function of the
	 * distances between the verbs, and the distance between the nouns</li>
	 * <li>link the found activity with the given activity</li>
	 * </ol>
	 */
	private Pair<UserActivity, Pair<String, String>> subsume(String verbSense,
			String nounSense) {

		double bestScore = Double.POSITIVE_INFINITY;
		UserActivity bestAct = null;
		String bestNounSense = null;
		String bestVerbSense = null;

		BreadthFirstShortestPath<String, DefaultEdge> nounShortestPaths = new BreadthFirstShortestPath<String, DefaultEdge>(
				nouns, nounSense);
		BreadthFirstShortestPath<String, DefaultEdge> verbShortestPaths = new BreadthFirstShortestPath<String, DefaultEdge>(
				verbs, verbSense);

		// Create single source shortest paths for all the verb and noun senses
		Map<String, BreadthFirstShortestPath<String, DefaultEdge>> nounOtherSP = new HashMap<String, BreadthFirstShortestPath<String, DefaultEdge>>();
		Map<String, BreadthFirstShortestPath<String, DefaultEdge>> verbOtherSP = new HashMap<String, BreadthFirstShortestPath<String, DefaultEdge>>();
		for (UserActivity other : activities) {
			String nounOther = other.getNoun();
			String verbOther = other.getVerb();

			if (senseMapNouns.containsKey(nounOther)) {
				for (String nounSenseOther : senseMapNouns.get(nounOther)) {
					if (nounOtherSP.containsKey(nounSenseOther))
						continue;
					BreadthFirstShortestPath<String, DefaultEdge> nounBFSP = new BreadthFirstShortestPath<String, DefaultEdge>(
							nouns, nounSenseOther);
					nounOtherSP.put(nounSenseOther, nounBFSP);
				}
			}
			if (senseMapVerbs.containsKey(verbOther)) {
				for (String verbSenseOther : senseMapVerbs.get(verbOther)) {
					if (verbOtherSP.containsKey(verbSenseOther))
						continue;
					BreadthFirstShortestPath<String, DefaultEdge> verbBFSP = new BreadthFirstShortestPath<String, DefaultEdge>(
							verbs, verbSenseOther);
					verbOtherSP.put(verbSenseOther, verbBFSP);
				}
			}
		}

		// Find an already existing activity which is closest to the (nounSense,
		// verbSense) activity
		for (UserActivity other : activities) {
			String nounOther = other.getNoun();
			String verbOther = other.getVerb();

			if (!senseMapNouns.containsKey(nounOther)
					|| !senseMapVerbs.containsKey(verbOther))
				continue;
			for (String nounSenseOther : senseMapNouns.get(nounOther)) {
				for (String verbSenseOther : senseMapVerbs.get(verbOther)) {
					if (Utils.wordName(nounSenseOther).equals(
							Utils.wordName(nounSense))
							&& Utils.wordName(verbSenseOther).equals(
									Utils.wordName(verbSense)))
						continue;

					double costNouns = 0;
					double costVerbs = 0;
					if (!nounSense.equals(nounSenseOther))
						costNouns = nounShortestPaths.getCost(nounSenseOther);
					if (!verbSense.equals(verbSenseOther))
						costVerbs = verbShortestPaths.getCost(verbSenseOther);

					// no path from given noun to other activity noun
					if (Double.isInfinite(costNouns)) {
						BreadthFirstShortestPath<String, DefaultEdge> nounSenseOtherSP = nounOtherSP
								.get(nounSenseOther);
						costNouns = nounSenseOtherSP.getCost(nounSense);
					}
					// no path from given verb to other activity verb
					if (Double.isInfinite(costVerbs)) {
						BreadthFirstShortestPath<String, DefaultEdge> verbSenseOtherSP = verbOtherSP
								.get(verbSenseOther);
						costVerbs = verbSenseOtherSP.getCost(verbSense);
					}

					// Verbs are more important, thus are weighted more
					double score = (costVerbs * 2.0 + costNouns * 1.0) / 3.0;

					if (score < bestScore) {
						bestScore = score;
						bestAct = other;
						bestNounSense = nounSenseOther;
						bestVerbSense = verbSenseOther;
					}
				}
			}
		}
		if (bestAct == null)
			return null;

		return new Pair<UserActivity, Pair<String, String>>(bestAct, subsume(
				verbSense, nounSense, bestNounSense, bestVerbSense));
	}

	/**
	 * Find an activity that subsumes <b>act1</b> and <b>act2</b>
	 */
	private Pair<String, String> subsume(String verbSense, String nounSense,
			String nounSenseOther, String verbSenseOther) {
		String nounLCA, verbLCA;
		if (nounSense.equals(nounSenseOther)) {
			nounLCA = nounSense;
		} else {
			Set<String> nounsLCASet = nounsLCA.onlineLCA(nounSense,
					nounSenseOther);
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
			Set<String> verbsLCASet = verbsLCA.onlineLCA(verbSense,
					verbSenseOther);
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
	 * Finds the activity that is the best match to the activity presented in
	 * addActivity.
	 * </p>
	 * <p/>
	 * <p>
	 * We define best match in this case to be the activity that is at the
	 * greatest depth in the verb and noun DAGs.
	 * </p>
	 */
	private void findBestMatch(UserActivity activity,
			List<UserActivity> matched, List<Pair<String, String>> parents) {
		if (parents.isEmpty() || parents == null)
			return;

		double bestDepth = Double.NEGATIVE_INFINITY;

		Pair<String, String> bestParentActivity = null;
		UserActivity bestMatchedActivity = null;

		// Find the parent activity which is deepest in the WordNet hierarchy
		for (int i = 0; i < parents.size(); ++i) {
			String subsumedVerb = parents.get(i).first;
			String subsumedNoun = parents.get(i).second;

			int depthNoun = depthMapNouns.get(subsumedNoun);
			int depthVerb = depthMapVerbs.get(subsumedVerb);
			double depth = Math.sqrt(depthNoun * depthNoun + depthVerb
					* depthVerb);

			if (depth > bestDepth) {
				bestDepth = depth;
				bestParentActivity = parents.get(i);
				bestMatchedActivity = matched.get(i);
			}
		}

		rebuildTree(activity, bestParentActivity, bestMatchedActivity);
	}

	private UserActivity rebuildTree(UserActivity activity,
			Pair<String, String> bestParentActivity,
			UserActivity bestMatchedActivity) {

		// Create a new parent activity
		String parentVerb = Utils.wordName(bestParentActivity.first);
		String parentNoun = Utils.wordName(bestParentActivity.second);

		List<String> locations = new ArrayList<String>();
		locations.addAll(activity.getLocations());
		for (String location : bestMatchedActivity.getLocations()) {
			if (!locations.contains(location))
				locations.add(location);
		}
		List<String> timesOfDay = new ArrayList<String>();
		locations.addAll(activity.getTimeOfDay());
		for (String timeOfDay : bestMatchedActivity.getTimeOfDay()) {
			if (!timesOfDay.contains(timeOfDay))
				timesOfDay.add(timeOfDay);
		}
		UserActivity parentActivity = new UserActivity(parentVerb, parentNoun,
				locations, timesOfDay, bestMatchedActivity.getAvgDuration());

		// Add (parent -> activity) and (parent -> bestMatchedActivity) to the
		// activity tree
		List<UserActivity> parents;
		parents = parentActivities.get(activity);
		parents.add(parentActivity);
		parentActivities.put(activity, parents);

		parents = parentActivities.get(bestMatchedActivity);
		parents.add(parentActivity);
		parentActivities.put(bestMatchedActivity, parents);

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

	private void addLocationToActivityMapping(UserActivity act) {
		List<String> locations = act.getLocations();
		for (String location : locations)
			addLocationToActivityMapping(act, location);
	}

	private void addLocationToActivityMapping(UserActivity act,
			List<String> locations) {
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
		return parentActivities.toString();
	}
}
