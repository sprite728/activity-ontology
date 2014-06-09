package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.TimeOfDay;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public interface ContextualActivity {
    String getVerb();

    String getNoun();

    int getMinDuration();

    int getMaxDuration();

    Set<String> getLocCategories();

    Set<TimeOfDay> getTimesOfDay();
}
