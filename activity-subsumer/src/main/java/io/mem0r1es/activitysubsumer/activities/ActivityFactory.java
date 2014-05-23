package io.mem0r1es.activitysubsumer.activities;

/**
 * @author Ivan Gavrilović
 */
public class ActivityFactory {
    public static AbstractActivity getActivity(String input) throws Exception {
        String type = input.substring(0, input.indexOf(" "));

        if (type.equals(SubsumedActivity.class.getCanonicalName())) {
            return new SubsumedActivity(input);
        } else if (type.equals(UserActivity.class.getCanonicalName())) {
            return new UserActivity(input);
        } else {
            throw new Exception("Unsupported activity type: " + input);
        }
    }
}
