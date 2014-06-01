package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;

/**
 * @author Ivan Gavrilović
 */
public class TimeParameter implements ContextualParameter{
    @Override
    public double getScore(AbstractActivity abstractActivity) {
        return 0;
    }
}
