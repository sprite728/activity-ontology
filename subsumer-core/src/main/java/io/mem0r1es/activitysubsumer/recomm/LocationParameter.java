package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityClassifier;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class LocationParameter implements ContextualParameter {
    private final String category;

    public LocationParameter(String category) {
        this.category = category;
    }

    @Override
    public double getScore(ContextualActivity contextualActivity) {
        Set<ContextualActivity> related = ActivityClassifier.getInstance().getAllActivities(category, true);

        return related.contains(contextualActivity) ? 1 : 0;
    }
}
