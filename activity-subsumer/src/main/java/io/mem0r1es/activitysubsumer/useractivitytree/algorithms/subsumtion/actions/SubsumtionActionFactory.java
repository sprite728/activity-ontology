package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;

public class SubsumtionActionFactory {
	/**
	 * If activity is equal to parentActivity, then an {@link InsertGenericActivity} is returned. If
	 * similarActivity is equal to parentActivity, then an {@link InsertSpecificActivity} is
	 * returned. Otherwise, a {@link CreateForkActivity} is returned.
	 * 
	 * @param activity
	 * @param similarActivity
	 * @param parentActivity
	 * @return The correct subsumtion action, based on the given input.
	 */
	public static final ISubsumtionAction createSubsumtionAction(UserActivity activity, UserActivity similarActivity, UserActivity parentActivity) {
		if (similarActivity.equals(parentActivity)) {
			return new InsertSpecificActivity(activity, parentActivity);
		} else if (activity.equals(parentActivity)) {
			return new InsertGenericActivity(activity, similarActivity);
		} else {
			return new CreateForkActivity(activity, parentActivity, similarActivity);
		}
	}
}