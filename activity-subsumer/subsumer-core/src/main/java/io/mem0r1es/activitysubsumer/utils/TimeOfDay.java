package io.mem0r1es.activitysubsumer.utils;

/**
 * @author Ivan Gavrilović
 */
public enum TimeOfDay {
    MORNING(0, "morning"), NOON(1, "noon"), AFTERNOON(2, "afternoon"), EVENING(3, "evening"), NIGHTTIME(4, "nighttime");

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
