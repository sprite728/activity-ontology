package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Default activity that was learned from the crowdsourcing experiment
 *
 * @author Ivan Gavrilović
 */
public class DefaultActivity extends BasicActivity implements ContextualActivity {

    private Set<String> locCategories;
    private Set<TimeOfDay> timesOfDay;
    private int minDuration;
    private int maxDuration;

    public DefaultActivity(String verb, String noun, Set<String> locCategories, Set<TimeOfDay> timesOfDay, String duration) {
        super(verb, noun);
        this.locCategories = locCategories;

        this.timesOfDay = timesOfDay;

        if (duration.equals("none")) {
            this.minDuration = 0;
            this.maxDuration = Integer.MAX_VALUE;
        } else {
            this.minDuration = Utils.parseMinutes(duration);

            int i = 0;
            while (Character.isDigit(duration.charAt(i++))) ;
            // position to the next number in 30m1h
            i++;
            if (i >= duration.length()) {
                this.maxDuration = this.minDuration;
            } else {
                this.maxDuration = Utils.parseMinutes(duration.substring(i));
            }
        }
    }

    @Override
    public int getMinDuration() {
        return minDuration;
    }

    @Override
    public int getMaxDuration() {
        return maxDuration;
    }

    @Override
    public Set<String> getLocCategories() {
        return locCategories;
    }

    @Override
    public Set<TimeOfDay> getTimesOfDay() {
        return timesOfDay;
    }
}
