package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

/**
 * Insert a more generic activity as <b>parent</b> of an existing activity.
 * 
 * @author horiaradu
 */
public class InsertGenericActivity implements ISubsumtionAction {
	private UserActivity childActivity;
	private UserActivity genericActivity;

	public InsertGenericActivity(UserActivity specificActivity, UserActivity parentActivity) {
		this.genericActivity = specificActivity;
		this.childActivity = parentActivity;
	}

	public void execute(UserActivityGraph graph) {
		graph.insertAbove(genericActivity, childActivity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childActivity == null) ? 0 : childActivity.hashCode());
		result = prime * result + ((genericActivity == null) ? 0 : genericActivity.hashCode());
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
		InsertGenericActivity other = (InsertGenericActivity) obj;
		if (childActivity == null) {
			if (other.childActivity != null)
				return false;
		} else if (!childActivity.equals(other.childActivity))
			return false;
		if (genericActivity == null) {
			if (other.genericActivity != null)
				return false;
		} else if (!genericActivity.equals(other.genericActivity))
			return false;
		return true;
	}
}