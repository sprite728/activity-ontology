package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;

/**
 * @author Ivan Gavrilović
 */
public interface ContextualParameter {
    /**
     * Get the value of how much the specified activity is aligned with the contextual parameter
     *
     * @param contextualActivity activity to check
     * @return how much activity is possible
     */
    double getScore(ContextualActivity contextualActivity);
}
