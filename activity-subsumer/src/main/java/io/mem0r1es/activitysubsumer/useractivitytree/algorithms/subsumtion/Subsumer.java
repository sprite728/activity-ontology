package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion;

import io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions.ISubsumtionAction;
import io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions.InsertFirstLevelActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions.SubsumtionActionFactory;
import io.mem0r1es.activitysubsumer.useractivitytree.algs.DistanceMeasurer;
import io.mem0r1es.activitysubsumer.useractivitytree.algs.PathBuilder;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;
import io.mem0r1es.activitysubsumer.useractivitytree.core.WordNetGraphs;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.GraphUtils;
import io.mem0r1es.activitysubsumer.useractivitytree.utils.Pair;
import io.mem0r1es.activitysubsumer.utils.Utils;

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

	private static final double SIMILARITY_THRESHOLD = 0.5;

	private final DirectedAcyclicGraph<String, DefaultEdge> nouns;
	private final DirectedAcyclicGraph<String, DefaultEdge> verbs;

	// Mappings between words and word senses
	private final Map<String, Set<String>> senseMapVerbs;
	private final Map<String, Set<String>> senseMapNouns;

	private PathBuilder<String, DefaultEdge> allVerbPaths;
	private DistanceMeasurer<String, DefaultEdge> verbDistances;

	private PathBuilder<String, DefaultEdge> allNounPaths;
	private DistanceMeasurer<String, DefaultEdge> nounDistances;

	public Subsumer() {
		nouns = WordNetGraphs.instance().getNouns();
		verbs = WordNetGraphs.instance().getVerbs();

		senseMapVerbs = WordNetGraphs.instance().getSenseMapVerbs();
		senseMapNouns = WordNetGraphs.instance().getSenseMapNouns();
	}

	/**
	 * Add an activity to the activity graph by trying to subsume. This means that before the
	 * activity is added, a similar activity is being searched for in the graph. If such an activity
	 * is present, then a more generic activity for the two is being build and all of these
	 * activities are inserted in the graph. (The more generic activity is the <b>parent</b> of the
	 * two activities: the given one and the similar one).
	 * 
	 * @throws IllegalArgumentException
	 *             if the noun and/or verb are not in the word net list
	 */
	public void addActivity(String verb, String noun, UserActivityGraph userActivityGraph) {
		UserActivity activity = new UserActivity(verb, noun);
		addActivity(activity, userActivityGraph);
	}

	/**
	 * Add an activity to the activity graph by trying to subsume. This means that before the
	 * activity is added, a similar activity is being searched for in the graph. If such an activity
	 * is present, then a more generic activity for the two is being build and all of these
	 * activities are inserted in the graph. (The more generic activity is the <b>parent</b> of the
	 * two activities: the given one and the similar one).
	 * 
	 * @throws IllegalArgumentException
	 *             if the noun and/or verb are not in the word net list
	 */
	public void addActivity(UserActivity activity, UserActivityGraph userActivityGraph) {
		checkActivityIntegrity(activity);

		// build the set of senses
		Set<String> verbSenses = senseMapVerbs.get(activity.getVerb());

		Set<String> allVerbSenses = new HashSet<String>();
		allVerbSenses.addAll(verbSenses);
		Set<String> existingVerbSenses = new HashSet<String>();

		for (UserActivity existingActivity : userActivityGraph.getNodes()) {
			if (existingActivity.equals(UserActivity.DEFAULT_NODE)) {
				continue;
			}
			Set<String> senses = senseMapVerbs.get(existingActivity.getVerb());
			existingVerbSenses.addAll(senses);
			allVerbSenses.addAll(senses);
		}

		// find all the paths from the fake root to all the verb senses
		allVerbPaths = new PathBuilder<String, DefaultEdge>(verbs, GraphUtils.DEFAULT_ROOT, allVerbSenses);
		verbDistances = new DistanceMeasurer<String, DefaultEdge>(allVerbPaths);

		// find the best existing verb senses and paths
		// map of existingVerbSense -> pairs of paths between root and existingVerbSense and the
		// verb we try to add
		Pair<Double, Map<String, Set<Pair<List<String>, List<String>>>>> bestPathsResult = findBestPaths(verbSenses, existingVerbSenses, verbDistances);
		Map<String, Set<Pair<List<String>, List<String>>>> bestVerbSenseToPath = bestPathsResult.second;
		double maxSimilarity = bestPathsResult.first;

		// if the similarity is under the threshold, add as child of root
		if (maxSimilarity < SIMILARITY_THRESHOLD) {
			userActivityGraph.add(activity);
		} else {
			// keep only the activities with the best matching verb senses
			Set<String> bestVerbs = new HashSet<String>();
			for (String bestVerbSense : bestVerbSenseToPath.keySet()) {
				bestVerbs.add(Utils.wordName(bestVerbSense));
			}
			Map<UserActivity, Set<UserActivity>> prunedActivities = userActivityGraph.getSubtreesByVerbs(bestVerbs);

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
			allNounPaths = new PathBuilder<String, DefaultEdge>(nouns, GraphUtils.DEFAULT_ROOT, allNounSenses);
			nounDistances = new DistanceMeasurer<String, DefaultEdge>(allNounPaths);

			// map of existingNounSense -> pairs of paths between root and existingNounSense and the
			// noun we try to add
			bestPathsResult = findBestPaths(nounSenses, existingNounSensesToActivityMap.keySet(), nounDistances);
			Map<String, Set<Pair<List<String>, List<String>>>> bestNounSenseToPath = bestPathsResult.second;
			maxSimilarity = bestPathsResult.first;

			if (maxSimilarity < SIMILARITY_THRESHOLD) {
				userActivityGraph.add(activity);
			}

			Set<ISubsumtionAction> actions = createSubsumtionActions(activity, bestNounSenseToPath, bestVerbSenseToPath, existingNounSensesToActivityMap);
			for (ISubsumtionAction action : actions) {
				action.execute(userActivityGraph);
			}
		}
	}

	/**
	 * Checks that the noun and the verb are present in WordNet
	 */
	private void checkActivityIntegrity(UserActivity activity) {
		String verb = activity.getVerb().toLowerCase().replace(' ', '_');
		String noun = activity.getNoun().toLowerCase().replace(' ', '_');

		// if we don't know about the noun and verb, throw exception
		if (!senseMapVerbs.containsKey(verb) || !senseMapNouns.containsKey(noun))
			throw new IllegalArgumentException("Unknown verb or noun");
	}

	/**
	 * The method will return the best pair of paths to a given sense and to an existing sense. It
	 * is possible that there are multiple equal pairs of paths for two senses and it is also
	 * possible that multiple different existing senses have equally good paths.
	 * 
	 * @param senses
	 *            - the set of senses for which we try to find "good" pairing senses
	 * @param existingSenses
	 *            - the set of available senses in which we find the "good" senses
	 * @param distanceMeasurer
	 *            - responsible for providing the best paris of paths between two given senses
	 * @return
	 */
	private Pair<Double, Map<String, Set<Pair<List<String>, List<String>>>>> findBestPaths(Set<String> senses, Set<String> existingSenses,
			DistanceMeasurer<String, DefaultEdge> distanceMeasurer) {
		// *****************find the best existing verb senses and paths
		// map of existingVerbSense -> pairs of paths between root and existingVerbSense and the
		// verb we try to add
		Map<String, Set<Pair<List<String>, List<String>>>> result = new HashMap<String, Set<Pair<List<String>, List<String>>>>();

		// distance is between 0 and 1
		double maxSimilarity = 0;
		for (String verbSense : senses) {
			for (String existingVerbSense : existingSenses) {
				Pair<Double, Set<Pair<List<String>, List<String>>>> bestPaths = distanceMeasurer.getBestPaths(verbSense, existingVerbSense);

				if (bestPaths.first > maxSimilarity) {
					maxSimilarity = bestPaths.first;
					result.clear();
					result.put(existingVerbSense, bestPaths.second);
				} else if (bestPaths.first == maxSimilarity) {
					result.put(existingVerbSense, bestPaths.second);
				}
			}
		}
		// **************************************************************
		return new Pair<Double, Map<String, Set<Pair<List<String>, List<String>>>>>(maxSimilarity, result);
	}

	/**
	 * find the farthest common node between two given paths
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
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

	/**
	 * Create the subtumtion actions for adding the given activity in the activity graph.
	 * 
	 * @param bestNounSenseToPath
	 *            - map from existing noun sense to a set of best pairs of paths to the given noun
	 *            sense and to the existing noun sense
	 * @param bestVerbSenseToPath
	 *            - map from existing verb sense to a set of best pairs of paths to the given verb
	 *            sense and to the existing verb sense
	 * @param existingNounSensesToActivityMap
	 *            - map from existing noun sense to an existing activity (A) in the activity graph;
	 *            A's verb is present in the bestVerbSenses and the existing noun sense is a sense
	 *            of a noun of an activity reachable from A
	 * @return
	 */
	private Set<ISubsumtionAction> createSubsumtionActions(UserActivity activity, Map<String, Set<Pair<List<String>, List<String>>>> bestNounSenseToPath,
			Map<String, Set<Pair<List<String>, List<String>>>> bestVerbSenseToPath, Map<String, UserActivity> existingNounSensesToActivityMap) {
		Set<ISubsumtionAction> result = new HashSet<ISubsumtionAction>();

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
							UserActivity parentActivity = new UserActivity(ancestorVerb, ancestorNoun);
							ISubsumtionAction action = SubsumtionActionFactory.createSubsumtionAction(activity, similarActivity, parentActivity);
							if (action != null) {
								result.add(action);
							}
							similarAndParentPair.add(new Pair<UserActivity, UserActivity>(similarActivity, parentActivity));
						}
					}
				}
			}
		}

		if (result.isEmpty()) {
			result.add(new InsertFirstLevelActivity(activity));
		}

		return result;
	}
}
