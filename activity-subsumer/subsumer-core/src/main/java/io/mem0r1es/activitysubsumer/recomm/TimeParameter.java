package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Ivan Gavrilović
 */
public class TimeParameter implements ContextualParameter {
    private static final Map<TimeOfDay, TimeParameter> instances = new EnumMap<TimeOfDay, TimeParameter>(TimeOfDay.class);

    private TimeOfDay timeOfDay;

    private TimeParameter(TimeOfDay t) {
        timeOfDay = t;
    }

    public static TimeParameter get(TimeOfDay t) {
        if (!instances.containsKey(t)) instances.put(t, new TimeParameter(t));

        return instances.get(t);
    }

    @Override
    public double getScore(ContextualActivity contextualActivity) {
        int diff = 0, paramOrder = timeOfDay.getOrder();
        for (TimeOfDay tod : contextualActivity.getTimesOfDay())
            diff = Math.abs(paramOrder - tod.getOrder());

        return diff / contextualActivity.getTimesOfDay().size();
    }
}
