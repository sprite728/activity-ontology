package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.TimeOfDay;

import java.util.Set;

/**
 * Contextual activity containing the noun, verb, min/max duration, location categories, and times of day when it
 * happened.
 *
 * @author Ivan Gavrilović
 */
public interface ContextualActivity {
    String getId();

    String getVerb();

    String getNoun();

    int getMinDuration();

    int getMaxDuration();

    /**
     * Return category names in lower case.
     * @return category names associated to this activity
     */
    Set<String> getLocCategories();

    Set<TimeOfDay> getTimesOfDay();
}
