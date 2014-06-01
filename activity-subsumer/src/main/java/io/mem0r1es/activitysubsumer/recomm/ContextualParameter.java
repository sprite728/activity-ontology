package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;

/**
 * @author Ivan Gavrilović
 */
public interface ContextualParameter {
    /**
     * Get the value of how much the specified activity is aligned with the contextual parameter
     * @param abstractActivity activity to check
     * @return how much activity is possible
     */
    double getScore(AbstractActivity abstractActivity);
}
