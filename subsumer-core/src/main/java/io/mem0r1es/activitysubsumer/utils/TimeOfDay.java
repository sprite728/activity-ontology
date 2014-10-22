package io.mem0r1es.activitysubsumer.utils;

/**
 * Represents the time of day. Four different periods are considered.
 *
 * @author Ivan Gavrilović
 */
public enum TimeOfDay {
    MORNING(0, "morning"), AFTERNOON(1, "afternoon"), EVENING(2, "evening"), NIGHTTIME(3, "nighttime");

    private int order;
    private String value;

    TimeOfDay(int order, String value) {
        this.order = order;
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public String getValue() {
        return value;
    }
}
