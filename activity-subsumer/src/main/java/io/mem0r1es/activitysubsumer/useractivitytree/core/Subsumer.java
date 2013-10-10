package io.mem0r1es.activitysubsumer.useractivitytree.core;

import io.mem0r1es.activitysubsumer.useractivitytree.algs.DistanceMeasurer;
import io.mem0r1es.activitysubsumer.useractivitytree.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.GraphUtils;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.Pair;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>
 * Subsumer class to perform the subsumption/aggregation presented here:
 * https://github.com/MEM0R1ES/ontology/wiki/Subsumer
 * </p>
 * 
 * @author horia
 */
public final class Subsumer implements Serializable {
	private static final long serialVersionUID = 0L;

	private static final double SIMILARITY_THRESHOLD = 0.2;

	private final DirectedAcyclicGraph<String, DefaultEdge> nouns;
	private final DirectedAcyclicGraph<String, DefaultEdge> verbs;

	private UserActivityTree userActivityTree;

	private final Map<String, List<UserActivity>> locationToActivityMapping;

	// Mappings between words and word senses
	private final Map<String, Set<String>> senseMapVerbs;
	private final Map<String, Set<String>> senseMapNouns;

	public Subsumer(InputStream nounIS, InputStream verbIS) throws IOException {
		nouns = GraphUtils.buildDAG(nounIS, DefaultEdge.class);
		verbs = GraphUtils.buildDAG(verbIS, DefaultEdge.class);

		senseMapVerbs = Utils.buildSenseMap(verbs);
		senseMapNouns = Utils.buildSenseMap(nouns);

		userActivityTree = new UserActivityTree();
		locationToActivityMapping = new HashMap<String, List<UserActivity>>();
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

		// if we don't know about the noun and verb, throw exception
		if (!senseMapVerbs.containsKey(verb) || !senseMapNouns.containsKey(noun))
			throw new IllegalArgumentException("Unknown verb or noun");

		UserActivity activity = new UserActivity(verb, noun);
		addActivityInternal(activity);
	}

	public void addActivityInternal(UserActivity activity) {
		if (userActivityTree.getNodes().contains(activity)) {
			return;
		}
		// build the set of senses
		Set<String> verbSenses = senseMapVerbs.get(activity.getVerb());

		Set<String> allVerbSenses = new HashSet<String>();
		allVerbSenses.addAll(verbSenses);
		Set<String> existingVerbSenses = new HashSet<String>();

		for (UserActivity existingActivity : userActivityTree.getNodes()) {
			if (existingActivity.equals(UserActivity.DEFAULT_NODE)) {
				continue;
			}
			Set<String> senses = senseMapVerbs.get(existingActivity.getVerb());
			existingVerbSenses.addAll(senses);
			allVerbSenses.addAll(senses);
		}

		// find all the paths from the fake root to all the verb senses
		PathBuilder<String, DefaultEdge> allVerbPaths = new PathBuilder<String, DefaultEdge>(verbs, GraphUtils.DEFAULT_ROOT, allVerbSenses);
		DistanceMeasurer<String, DefaultEdge> verbDistances = new DistanceMeasurer<String, DefaultEdge>(allVerbPaths);

		// *****************find the best existing verb senses and paths
		// map of existingVerbSense -> pairs of paths between root and existingVerbSense and the
		// verb we try to add
		Map<String, Set<Pair<List<String>, List<String>>>> bestVerbSenseToPath = new HashMap<String, Set<Pair<List<String>, List<String>>>>();

		// distance is between 0 and 1
		double maxSimilarity = 0;
		for (String verbSense : verbSenses) {
			for (String existingVerbSense : existingVerbSenses) {
				Pair<Double, Set<Pair<List<String>, List<String>>>> bestPaths = verbDistances.getBestPaths(verbSense, existingVerbSense);

				if (bestPaths.first > maxSimilarity) {
					maxSimilarity = bestPaths.first;
					bestVerbSenseToPath.clear();
					bestVerbSenseToPath.put(existingVerbSense, bestPaths.second);
				} else if (bestPaths.first == maxSimilarity) {
					bestVerbSenseToPath.put(existingVerbSense, bestPaths.second);
				}
			}
		}
		// **************************************************************

		// if the similarity is under the threshold, add as child of root
		if (maxSimilarity < SIMILARITY_THRESHOLD) {
			userActivityTree.add(activity);
		} else {
			// keep only the activities with the best matching verb senses
			Set<String> bestVerbs = new HashSet<String>();
			for (String bestVerbSense : bestVerbSenseToPath.keySet()) {
				bestVerbs.add(Utils.wordName(bestVerbSense));
			}
			Map<UserActivity, Set<UserActivity>> prunedActivities = userActivityTree.getSubtreesByVerbs(bestVerbs);

			Set<String> nounSenses = senseMapNouns.get(activity.getNoun());

			Set<String> allNounSenses = new HashSet<String>();
			allNounSenses.addAll(nounSenses);
			// map from noun sense to activity subtree root
			Map<String, UserActivity> existingNounSensesToActivityMap = new HashMap<String, UserActivity>();

			for (Entry<UserActivity, Set<UserActivity>> entry : prunedActivities.entrySet()) {
				for (UserActivity existingActivity : entry.getValue()) {
					Set<String> senses = senseMapNouns.get(existingActivity.getNoun());
					for (String existingSense : senses) {
						existingNounSensesToActivityMap.put(existingSense, entry.getKey());
					}
					allNounSenses.addAll(senses);
				}
			}

			// find all the paths from the fake root to all the verb senses
			PathBuilder<String, DefaultEdge> allNounPaths = new PathBuilder<String, DefaultEdge>(nouns, GraphUtils.DEFAULT_ROOT, allNounSenses);
			DistanceMeasurer<String, DefaultEdge> nounDistances = new DistanceMeasurer<String, DefaultEdge>(allNounPaths);

			// map of existingNounSense -> pairs of paths between root and existingNounSense and the
			// noun we try to add
			Map<String, Set<Pair<List<String>, List<String>>>> bestNounSenseToPath = new HashMap<String, Set<Pair<List<String>, List<String>>>>();

			// distance is between 0 and 1
			maxSimilarity = 0;
			for (String nounSense : nounSenses) {
				for (String existingNounSense : existingNounSensesToActivityMap.keySet()) {
					Pair<Double, Set<Pair<List<String>, List<String>>>> bestPaths = nounDistances.getBestPaths(nounSense, existingNounSense);

					if (bestPaths.first > maxSimilarity) {
						maxSimilarity = bestPaths.first;
						bestNounSenseToPath.clear();
						bestNounSenseToPath.put(existingNounSense, bestPaths.second);
					} else if (bestPaths.first == maxSimilarity) {
						bestNounSenseToPath.put(existingNounSense, bestPaths.second);
					}
				}
			}

			if (maxSimilarity < SIMILARITY_THRESHOLD) {
				userActivityTree.add(activity);
			}
			
			Set<Pair<UserActivity, UserActivity>> similarAndParentPair = new HashSet<Pair<UserActivity, UserActivity>>();

			// for each best noun sense
			for (Entry<String, Set<Pair<List<String>, List<String>>>> nounEntry : bestNounSenseToPath.entrySet()) {
				String bestNounSense = nounEntry.getKey();
				// the root of the subgraph in which we have found this sense
				UserActivity similarActivity = existingNounSensesToActivityMap.get(bestNounSense);

				Set<String> ancestorNouns = new HashSet<String>();
				// for each possible pairs of paths in the noun graph, find the ancestor noun
				for (Pair<List<String>, List<String>> pair : nounEntry.getValue()) {
					String commonAncestor = commonAncestor(pair.first, pair.second);
					if (commonAncestor != null) {
						ancestorNouns.add(Utils.wordName(commonAncestor));
					}
				}

				// for each best possible verb sense
				for (Entry<String, Set<Pair<List<String>, List<String>>>> verbEntry : bestVerbSenseToPath.entrySet()) {
					String bestVerbSense = verbEntry.getKey();
					// only if this is the root activity of the subtree
					if (Utils.wordName(bestVerbSense).equals(similarActivity.getVerb())) {

						// find the ancestor verbs
						Set<String> ancestorVerbs = new HashSet<String>();
						for (Pair<List<String>, List<String>> pair : verbEntry.getValue()) {
							String commonAncestor = commonAncestor(pair.first, pair.second);
							if (commonAncestor != null) {
								ancestorVerbs.add(Utils.wordName(commonAncestor));
							}
						}

						for (String ancestorNoun : ancestorNouns) {
							for (String ancestorVerb : ancestorVerbs) {
								similarAndParentPair.add(new Pair<UserActivity, UserActivity>(similarActivity, new UserActivity(ancestorVerb, ancestorNoun)));
							}
						}
					}
				}
			}

			subsume(activity, similarAndParentPair);
		}
	}

	private void subsume(UserActivity activity, Set<Pair<UserActivity, UserActivity>> similarAndParentPair) {
		// must add the activity and FIRST as brothers, all children of SECOND

		if (similarAndParentPair.isEmpty()) {
			userActivityTree.add(activity);
		} else {
			for (Pair<UserActivity, UserActivity> pair : similarAndParentPair) {
				if (activity.toString().equals("get,paint") || pair.first.toString().equals("get,paint") || pair.second.toString().equals("get,paint")) {
					System.out.println();
				}

				// activity can be equal to SECOND -> userActivity.add(FIRST, activity)
				// activity can't be equal to FIRST -> need for test at the beginning of addActivity
				// FIRST can be equal to SECOND -> userActivity.add(activity, SECOND)
				if (pair.first.equals(pair.second)) {
					userActivityTree.add(activity, pair.second);
				} else if (activity.equals(pair.second)) {
					userActivityTree.add(pair.first, activity);
				} else {
					userActivityTree.add(activity, pair.second);
					userActivityTree.add(pair.first, pair.second);
				}
			}
		}
	}

	private String commonAncestor(List<String> first, List<String> second) {
		List<String> shortest, longest;
		if (first.size() > second.size()) {
			shortest = second;
			longest = first;
		} else {
			shortest = first;
			longest = second;
		}

		Iterator<String> longIterator = longest.iterator();
		String lastCommonNode = null;
		int level = 0;
		for (String node : shortest) {
			if (node.equals(longIterator.next()) == false) {
				break;
			}
			lastCommonNode = node;
			level++;
		}
		if (level <= 2) {
			return null;
		}
		return lastCommonNode;
	}

	@Override
	public String toString() {
		return userActivityTree.getParentRelations().toString();
	}
}
