package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.Cons;

import java.io.Serializable;
import java.util.*;

/**
 * Class representing the user activity.
 *
 * @author Sebastian Claici
 *
 * Changes: Ivan Gavrilovic
 */
public final class UserActivity extends AbstractActivity{
    private Set<String> locations;
    private Set<String> timeOfDay;

    private String avgDuration;
    /**
     * Default value for the score
     */
    private double score = 1;


    /**
     * <p>
     * Creates an <b>UserActivity</b> object with all the required data.
     * </p>
     * <p>
     * Note that this constructor assumes each activity is specified as in the
     * <i>activities.graph</i> file.
     * </p>
     */
    public UserActivity(String id, String verb, String noun, String location, String timeOfDay, String avgDuration) {
        super(id, verb, noun);

        this.locations = new HashSet<String>();
        this.timeOfDay = new HashSet<String>();
        Collections.addAll(this.locations, location.split(Cons.ENTRY_SEPARATOR_REG));
        Collections.addAll(this.timeOfDay, timeOfDay.split(Cons.ENTRY_SEPARATOR_REG));
        this.avgDuration = avgDuration;
    }

    public UserActivity(String id, String verb, String noun, Set<String> locations, Set<String> timesOfDay, String avgDuration) {
        super(id, verb, noun);

        this.locations = new HashSet<String>();
        this.timeOfDay = new HashSet<String>();
        this.locations.addAll(locations);
        this.timeOfDay.addAll(timesOfDay);
        this.avgDuration = avgDuration;
    }

    public UserActivity(String id, String verb, String noun) {
        super(id, verb, noun);

        this.locations = new HashSet<String>();
        this.timeOfDay = new HashSet<String>();
        this.avgDuration = "n/a";
    }

    public UserActivity(String serializedInput) {
        super(serializedInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserActivity that = (UserActivity) o;

        if (Double.compare(that.score, score) != 0) return false;
        if (!avgDuration.equals(that.avgDuration)) return false;
        if (!locations.equals(that.locations)) return false;
        if (!timeOfDay.equals(that.timeOfDay)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + locations.hashCode();
        result = 31 * result + timeOfDay.hashCode();
        result = 31 * result + avgDuration.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public void deSerialize(String input) {
        String parts[] = decodeParts(input);

        if (!parts[0].equals(UserActivity.class.getSimpleName())){
            System.err.println("Deserializing to UserActivity ERROR for: "+input);

        }

        id = parts[1];
        verb = parts[2];
        noun = parts[3];

        this.locations = new HashSet<String>();
        this.timeOfDay = new HashSet<String>();
        Collections.addAll(locations, parts[4].split(Cons.ENTRY_SEPARATOR_REG));
        Collections.addAll(timeOfDay, parts[5].split(Cons.ENTRY_SEPARATOR_REG));

        score = Double.parseDouble(parts[6]);
        avgDuration = parts[7];
    }

    @Override
    public String serialize() {
        String output = super.serialize();
        List<String> parts = new LinkedList<String>();

        // add all locations
        String locs = "";
        for (String s : locations) {
            locs += s + Cons.ENTRY_SEPARATOR;
        }
        parts.add(locs);

        // add all time periods
        String tms = "";
        for (String s : timeOfDay) {
            tms += s + Cons.ENTRY_SEPARATOR;
        }
        parts.add(tms);

        // add score and avg duration
        parts.add(Double.toString(score));
        parts.add(avgDuration);

        return output + encodeParts(parts);
    }
}
