package io.mem0r1es.activitysubsumer.useractivitytree.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserActivityTree {
	private final Set<UserActivity> activities = new HashSet<UserActivity>();
	private final Map<UserActivity, Set<UserActivity>> parentActivities = new HashMap<UserActivity, Set<UserActivity>>();

	public UserActivityTree() {
		activities.add(UserActivity.DEFAULT_NODE);
	}

	public void add(UserActivity activity) {
		activities.add(activity);
		Set<UserActivity> parents = new HashSet<UserActivity>();
		parents.add(UserActivity.DEFAULT_NODE);
		parentActivities.put(activity, parents);
	}

	public Set<UserActivity> getNodes() {
		return activities;
	}

	public void add(UserActivity activity, UserActivity parentActivity) {
		activities.add(activity);
		activities.add(parentActivity);
		Set<UserActivity> parents = parentActivities.get(activity);
		if (parents == null) {
			parents = new HashSet<UserActivity>();
			parentActivities.put(activity, parents);
		}
		parents.add(parentActivity);
	}

	public Map<UserActivity, Set<UserActivity>> getParentRelations() {
		return parentActivities;
	}
}