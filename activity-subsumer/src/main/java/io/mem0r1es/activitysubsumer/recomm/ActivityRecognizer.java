package io.mem0r1es.activitysubsumer.recomm;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;

import java.util.*;

/**
 * This class uses the contextual information in order to provide the user with the set of candidate
 * activities
 *
 * @author Ivan Gavrilović
 */
public class ActivityRecognizer {
    private Set<AbstractActivity> allActivities;
    private Set<ContextualParameter> contextualParameters;

    public ActivityRecognizer(Set<AbstractActivity> allActivities, Set<ContextualParameter> contextualParameters) {
        this.allActivities = allActivities;
        this.contextualParameters = contextualParameters;
    }

    /**
     * Applies the contextual parameters and returns the list of candidate activities sorted descending
     * @return set of sorted candidate activities
     */
    public Set<AbstractActivity> apply() {
        List<AbstractActivitySortable> activitiesSortable = new LinkedList<AbstractActivitySortable>();

        for(AbstractActivity aa:allActivities){
            double score = 0;
            for(ContextualParameter cp:contextualParameters){
                score += cp.getScore(aa);
            }
            activitiesSortable.add(new AbstractActivitySortable(aa, score));
        }

        Collections.sort(activitiesSortable);

        Set<AbstractActivity> result = new HashSet<AbstractActivity>();
        for(AbstractActivitySortable aas: activitiesSortable){
            result.add(aas.activity);
        }
        return result;
    }

    class AbstractActivitySortable implements Comparable {
        private AbstractActivity activity;
        private double score;

        AbstractActivitySortable(AbstractActivity activity, double score) {
            this.activity = activity;
            this.score = score;
        }

        @Override
        public int compareTo(Object o) {
            AbstractActivitySortable other = (AbstractActivitySortable) o;
            return score > other.score ? 1 : (score == other.score ? 0 : -1);
        }
    }
}
