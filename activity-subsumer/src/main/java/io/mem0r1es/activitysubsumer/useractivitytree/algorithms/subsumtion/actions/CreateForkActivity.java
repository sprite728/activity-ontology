package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

/**
 * Insert a specific activity as a sibling of an existing activity. Also insert a more generic
 * activity as their new <b>parent</b>.
 * 
 * @author horiaradu
 */
public class CreateForkActivity implements ISubsumtionAction {
	private UserActivity specificActivity;
	private UserActivity genericActivity;
	private UserActivity existingActivity;

	public CreateForkActivity(UserActivity specificActivity, UserActivity genericActivity, UserActivity existingActivity) {
		this.genericActivity = genericActivity;
		this.specificActivity = specificActivity;
		this.existingActivity = existingActivity;
	}

	public void execute(UserActivityGraph graph) {
		graph.insertAbove(genericActivity, existingActivity);
		graph.addChild(specificActivity, genericActivity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((existingActivity == null) ? 0 : existingActivity.hashCode());
		result = prime * result + ((genericActivity == null) ? 0 : genericActivity.hashCode());
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
		CreateForkActivity other = (CreateForkActivity) obj;
		if (existingActivity == null) {
			if (other.existingActivity != null)
				return false;
		} else if (!existingActivity.equals(other.existingActivity))
			return false;
		if (genericActivity == null) {
			if (other.genericActivity != null)
				return false;
		} else if (!genericActivity.equals(other.genericActivity))
			return false;
		if (specificActivity == null) {
			if (other.specificActivity != null)
				return false;
		} else if (!specificActivity.equals(other.specificActivity))
			return false;
		return true;
	}

}