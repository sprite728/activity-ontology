package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

/**
 * Insert a specific activity as <b>child</b> of an existing activity
 * 
 * @author horiaradu
 */
public class InsertSpecificActivity implements ISubsumtionAction {
	private UserActivity parentActivity;
	private UserActivity specificActivity;

	public InsertSpecificActivity(UserActivity specificActivity, UserActivity parentActivity) {
		this.specificActivity = specificActivity;
		this.parentActivity = parentActivity;
	}

	public void execute(UserActivityGraph graph) {
		graph.addChild(specificActivity, parentActivity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentActivity == null) ? 0 : parentActivity.hashCode());
		result = prime * result + ((specificActivity == null) ? 0 : specificActivity.hashCode());
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
		InsertSpecificActivity other = (InsertSpecificActivity) obj;
		if (parentActivity == null) {
			if (other.parentActivity != null)
				return false;
		} else if (!parentActivity.equals(other.parentActivity))
			return false;
		if (specificActivity == null) {
			if (other.specificActivity != null)
				return false;
		} else if (!specificActivity.equals(other.specificActivity))
			return false;
		return true;
	}

}