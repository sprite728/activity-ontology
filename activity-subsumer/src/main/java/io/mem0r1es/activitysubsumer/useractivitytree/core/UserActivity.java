package io.mem0r1es.activitysubsumer.useractivitytree.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper class for activities.
 *
 * @author Sebastian Claici
 */
public final class UserActivity implements Serializable {
    private final String verb;
    private final String noun;
    private final List<String> locations;
    private final List<String> timeOfDay;

    private double score;
    private String avgDuration;

    private static final long serialVersionUID = 7526471156722776147L;

    /**
     * <p>Creates an <b>UserActivity</b> object with all the required data.</p><p>Note that this constructor assumes each activity is
     * specified as in the <i>activities.graph</i> file.</p>
     */
    public UserActivity(String verb, String noun, String location, String timeOfDay, String avgDuration) {
        this.verb = verb;
        this.noun = noun;
        this.avgDuration = avgDuration;

        this.locations = Arrays.asList(location.split("[|]"));
        this.timeOfDay = Arrays.asList(timeOfDay.split("[|]"));
        this.score = 1;
    }

    public UserActivity(String verb, String noun, List<String> locations, List<String> timesOfDay, String avgDuration) {
        this.verb = verb;
        this.noun = noun;
        this.locations = locations;
        this.timeOfDay = timesOfDay;
        this.avgDuration = avgDuration;
    }

    public UserActivity(String verb, String noun) {
        this.verb = verb;
        this.noun = noun;

        this.locations = new ArrayList<String>();
        this.timeOfDay = new ArrayList<String>();
        this.score = 1;
    }

    public String getVerb() {
        return verb;
    }

    public String getNoun() {
        return noun;
    }

    public String getAvgDuration() {
        return avgDuration;
    }

    public List<String> getLocations() {
        return locations;
    }

    public List<String> getTimeOfDay() {
        return timeOfDay;
    }

    public double getScore() {
        return score;
    }

    public void setAvgDuration(String avgDuration) {
        this.avgDuration = avgDuration;
    }

    public void addLocation(String location) {
        locations.add(location);
    }

    public void addTimeOfDay(String timeOfDay) {
        this.timeOfDay.add(timeOfDay);
    }

    public void augmentScore(double factor) {
        score = score * factor;
    }

    @Override
    public String toString() {
        return verb + "," + noun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserActivity userActivity = (UserActivity) o;

        if (!noun.equals(userActivity.noun)) return false;
        if (!verb.equals(userActivity.verb)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = verb.hashCode();
        result = 31 * result + noun.hashCode();
        return result;
    }
}
