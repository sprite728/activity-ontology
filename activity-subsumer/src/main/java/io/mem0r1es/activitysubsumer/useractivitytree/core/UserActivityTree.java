package io.mem0r1es.activitysubsumer.useractivitytree.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserActivityTree {
	private final Set<UserActivity> activities = new HashSet<UserActivity>();
	private final Map<UserActivity, Set<UserActivity>> parentActivities = new HashMap<UserActivity, Set<UserActivity>>();
	private final Map<UserActivity, Set<UserActivity>> childrenActivities = new HashMap<UserActivity, Set<UserActivity>>();

	public UserActivityTree() {
		activities.add(UserActivity.DEFAULT_NODE);
	}

	public void add(UserActivity activity) {
		activities.add(activity);
		Set<UserActivity> parents = new HashSet<UserActivity>();
		parents.add(UserActivity.DEFAULT_NODE);
		parentActivities.put(activity, parents);
		Set<UserActivity> children = childrenActivities.get(UserActivity.DEFAULT_NODE);
		if (children == null) {
			children = new HashSet<UserActivity>();
			childrenActivities.put(UserActivity.DEFAULT_NODE, children);
		}
		children.add(activity);
	}

	public Set<UserActivity> getNodes() {
		return activities;
	}

	// public void add(UserActivity activity, UserActivity parentActivity) {
	// activities.add(activity);
	// activities.add(parentActivity);
	// Set<UserActivity> parents = parentActivities.get(activity);
	// if (parents == null) {
	// parents = new HashSet<UserActivity>();
	// parentActivities.put(activity, parents);
	// } else {
	//
	// }
	// parents.add(parentActivity);
	// }

	public Map<UserActivity, Set<UserActivity>> getParentRelations() {
		return parentActivities;
	}

	public UserActivity getNode(String verb, String noun) {
		for (UserActivity activity : activities) {
			if (activity.getVerb().equals(verb) && activity.getNoun().equals(noun)) {
				return activity;
			}
		}
		return null;
	}

	public void add(UserActivity activity, UserActivity parentActivity) {
		activities.add(activity);
		activities.add(parentActivity);
		Set<UserActivity> activityOldParents = parentActivities.get(activity);
		Set<UserActivity> parentActivityParents = parentActivities.get(parentActivity);
		if (activityOldParents == null) {
			activityOldParents = new HashSet<UserActivity>();
			parentActivities.put(activity, activityOldParents);
		}
		if (parentActivityParents == null) {
			parentActivityParents = new HashSet<UserActivity>();
			parentActivities.put(parentActivity, parentActivityParents);
		}
		parentActivityParents.addAll(activityOldParents);
		activityOldParents.clear();
		activityOldParents.add(parentActivity);

		Set<UserActivity> parentActivityChildren = childrenActivities.get(parentActivity);
		Set<UserActivity> activityChildren = childrenActivities.get(activity);
		if (activityChildren == null) {
			activityChildren = new HashSet<UserActivity>();
			childrenActivities.put(activity, activityChildren);
		}
		if (parentActivityChildren == null) {
			parentActivityChildren = new HashSet<UserActivity>();
			childrenActivities.put(parentActivity, parentActivityChildren);
		}
		activityChildren.addAll(parentActivityChildren);
		parentActivityChildren.clear();
		parentActivityChildren.add(activity);
	}

	public Map<UserActivity, Set<UserActivity>> getSubtreesByVerbs(Set<String> rootVerbs) {
		Map<UserActivity, Set<UserActivity>> result = new HashMap<UserActivity, Set<UserActivity>>();
		for (String rootVerb : rootVerbs) {
			for (UserActivity activity : activities) {
				if (activity.getVerb().equals(rootVerb)) {
					result.put(activity, getSubtree(activity));
				}
			}
		}
		return result;
	}

	public Set<UserActivity> getSubtree(UserActivity root) {
		Set<UserActivity> result = new HashSet<UserActivity>();
		result.add(root);
		result.addAll(getChildrenRecursively(root));
		return result;
	}

	private Set<UserActivity> getChildrenRecursively(UserActivity activity) {
		Set<UserActivity> result = new HashSet<UserActivity>();
		Set<UserActivity> children = childrenActivities.get(activity);
		if (children != null) {
			result.addAll(children);
			for (UserActivity child : children) {
				result.addAll(getChildrenRecursively(child));
			}
		}
		return result;
	}
}