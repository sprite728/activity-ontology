package io.mem0r1es.activitysubsumer.activities;

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
    public static AbstractActivity getActivity(String input) throws Exception {
        String type = input.substring(0, input.indexOf(" "));

        if (type.equals(SubsumedActivity.class.getSimpleName())) {
            return new SubsumedActivity(input);
        } else if (type.equals(UserActivity.class.getSimpleName())) {
            return new UserActivity(input);
        } else {
            throw new Exception("Unsupported activity type: " + input);
        }
    }
}
