package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

/**
 * Insert an activity as a first level activity.
 * 
 * @author horiaradu
 */
public class InsertFirstLevelActivity implements ISubsumtionAction {
	private UserActivity activity;

	public InsertFirstLevelActivity(UserActivity activity) {
		this.activity = activity;
	}

	public void execute(UserActivityGraph graph) {
		graph.addChild(activity, UserActivity.DEFAULT_NODE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InsertFirstLevelActivity other = (InsertFirstLevelActivity) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		return true;
	}
}