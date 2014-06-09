package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.utils.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Factory for the activities
 * @author Ivan Gavrilović
 */
public class ActivityFactory {

    /**
     * Creates the co-responding activity based on the serialized input
     * @param input input to serialize
     * @return co-responding activity object
     * @throws Exception in case that the input is not serializable
     */
    public static ContextualActivity deserialize(String input) throws Exception {
        String type = input.substring(0, input.indexOf(" "));

        if (type.equals(UserActivity.class.getSimpleName())) {
            return new UserActivity(input);
        } else {
            throw new Exception("Unsupported activity type: " + input);
        }
    }

    public static String serialize(ContextualActivity activity){
        if (activity instanceof UserActivity){
            return ((UserActivity) activity).serialize();
        }
        else{
            // default serialization
            List<String> parts = new LinkedList<String>();
            parts.add(activity.getClass().getSimpleName());
            parts.add(Integer.toString(activity.hashCode()));
            parts.add(activity.getVerb());
            parts.add(activity.getNoun());
            return Utils.encodeParts(parts);
        }
    }
}
