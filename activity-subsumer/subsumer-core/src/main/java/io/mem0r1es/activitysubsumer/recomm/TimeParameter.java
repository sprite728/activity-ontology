package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;

/**
 * @author Ivan Gavrilović
 */
public class TimeParameter implements ContextualParameter {
    @Override
    public double getScore(ContextualActivity contextualActivity) {
        return 0;
    }
}
