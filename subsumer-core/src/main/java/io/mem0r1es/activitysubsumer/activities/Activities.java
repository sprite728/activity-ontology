package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Factory for the activities
 *
 * @author Ivan Gavrilović
 */
public class Activities {

    /**
     * Creates the co-responding activity based on the serialized input
     *
     * @param input input to serialize
     * @return corresponding activity object
     * @throws IOException in case that the input is not serializable
     */
    public static ContextualActivity deserialize(String input) throws IOException {
        String type = input.substring(0, input.indexOf(" "));

        if (type.equals(UserActivity.class.getSimpleName())) {
            return new UserActivity(input);
        } else {
            throw new IOException("Unsupported activity type: " + input);
        }
    }

    /**
     * Serialize the activity. Class should implement the serialize method, otherwise,
     * the default serializer is used.
     *
     * @param activity activity
     * @return serialized activity
     */
    public static String serialize(ContextualActivity activity) {
        if (activity instanceof UserActivity) {
            return ((UserActivity) activity).serialize();
        } else {
            // default serialization
            List<String> parts = new LinkedList<String>();
            parts.add(activity.getClass().getSimpleName());
            parts.add(Integer.toString(activity.hashCode()));
            parts.add(activity.getVerb());
            parts.add(activity.getNoun());
            return Utils.encodeParts(parts);
        }
    }

    /**
     * Creates new {@link io.mem0r1es.activitysubsumer.activities.BasicActivity}
     * @param verb verbs of activity
     * @param noun noun of activity
     * @return created basic activity
     */
    public static BasicActivity basic(String verb, String noun){
        return new BasicActivity(verb, noun);
    }
}