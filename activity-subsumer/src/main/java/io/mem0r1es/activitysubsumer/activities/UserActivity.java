package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.Cons;

import java.io.Serializable;
import java.util.*;

/**
 * Wrapper class for activities.
 *
 * @author Sebastian Claici
 */
public final class UserActivity extends AbstractActivity{
    private final Set<String> locations = new HashSet<String>();
    private final Set<String> timeOfDay = new HashSet<String>();

    private double score = 1;
    private String avgDuration = "n/a";

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

        Collections.addAll(this.locations, location.split(Cons.ENTRY_SEPARATOR));
        Collections.addAll(this.timeOfDay, location.split(Cons.ENTRY_SEPARATOR));
        this.avgDuration = avgDuration;
    }

    public UserActivity(String id, String verb, String noun, Set<String> locations, Set<String> timesOfDay, String avgDuration) {
        super(id, verb, noun);
        this.locations.addAll(locations);
        this.timeOfDay.addAll(timesOfDay);
        this.avgDuration = avgDuration;
    }

    public UserActivity(String id, String verb, String noun) {
        super(id, verb, noun);
    }

    public UserActivity(String serializedInput) {
        super(serializedInput);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserActivity other = (UserActivity) obj;
        if (noun == null) {
            if (other.noun != null)
                return false;
        } else if (!noun.equals(other.noun))
            return false;
        if (verb == null) {
            if (other.verb != null)
                return false;
        } else if (!verb.equals(other.verb))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((noun == null) ? 0 : noun.hashCode());
        result = prime * result + ((verb == null) ? 0 : verb.hashCode());
        return result;
    }

    @Override
    public void deSerialize(String input) {
        String parts[] = decodeParts(input);

        if (!parts[0].equals(UserActivity.class.getCanonicalName())){
            System.err.println("Deserializing to UserActivity ERROR for: "+input);

        }

        id = parts[1];
        verb = parts[2];
        noun = parts[3];

        Collections.addAll(locations, parts[4].split(Cons.ENTRY_SEPARATOR));
        Collections.addAll(timeOfDay, parts[5].split(Cons.ENTRY_SEPARATOR));

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
