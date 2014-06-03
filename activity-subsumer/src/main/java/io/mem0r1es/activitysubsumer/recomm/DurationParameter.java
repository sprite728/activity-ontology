package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;

/**
 * @author Ivan Gavrilović
 */
public class DurationParameter implements ContextualParameter {
    private int durationInMinutes;

    public DurationParameter(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public double getScore(ContextualActivity contextualActivity) {
        if (durationInMinutes < contextualActivity.getMaxDuration() && durationInMinutes > contextualActivity.getMinDuration()) {
            return 1.0;
        } else return 0.0;
    }
}
