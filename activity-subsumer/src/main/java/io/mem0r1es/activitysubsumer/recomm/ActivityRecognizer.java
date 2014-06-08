package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;

import java.util.*;

/**
 * This class uses the contextual information in order to provide the user with the set of candidate
 * activities
 *
 * @author Ivan Gavrilović
 */
public class ActivityRecognizer {
    private Set<ContextualActivity> allActivities;
    private Set<ContextualParameter> contextualParameters;

    public ActivityRecognizer(Set<ContextualActivity> allActivities, Set<ContextualParameter> contextualParameters) {
        this.allActivities = allActivities;
        this.contextualParameters = contextualParameters;
    }

    /**
     * Applies the contextual parameters and returns the list of candidate activities sorted descending
     * @return set of sorted candidate activities
     */
    public Set<ContextualActivity> candidates() {
        List<ContextualActivitySortable> activitiesSortable = new LinkedList<ContextualActivitySortable>();

        for(ContextualActivity aa:allActivities){
            double score = 0;
            for(ContextualParameter cp:contextualParameters){
                score += cp.getScore(aa);
            }
            activitiesSortable.add(new ContextualActivitySortable(aa, score));
        }
        Collections.sort(activitiesSortable);

        Set<ContextualActivity> result = new HashSet<ContextualActivity>();
        for(ContextualActivitySortable aas: activitiesSortable){
            result.add(aas.activity);
        }
        return result;
    }

    class ContextualActivitySortable implements Comparable {
        private ContextualActivity activity;
        private double score;

        ContextualActivitySortable(ContextualActivity activity, double score) {
            this.activity = activity;
            this.score = score;
        }

        @Override
        public int compareTo(Object o) {
            ContextualActivitySortable other = (ContextualActivitySortable) o;
            return score > other.score ? 1 : (score == other.score ? 0 : -1);
        }
    }
}
