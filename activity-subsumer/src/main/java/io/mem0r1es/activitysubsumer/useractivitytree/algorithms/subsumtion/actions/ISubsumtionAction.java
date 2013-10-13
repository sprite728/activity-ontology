package io.mem0r1es.activitysubsumer.useractivitytree.algorithms.subsumtion.actions;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

/**
 * Interface that each subsumtion action must implement.
 * 
 * @author horia
 */
public interface ISubsumtionAction {
	/**
	 * Performs the subsumtion action on the given graph.
	 * 
	 * @param graph
	 */
	public void execute(UserActivityGraph graph);
}