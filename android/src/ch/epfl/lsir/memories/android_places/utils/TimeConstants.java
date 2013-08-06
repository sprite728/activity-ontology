package ch.epfl.lsir.memories.android_places.utils;

/**
 * Constants to represent time intervals.
 *
 * @author Sebastian Claici
 */
public enum TimeConstants {
    MILLISECONDS_PER_SECOND(1000), UPDATE_INTERVAL_IN_SECONDS(10),
    UPDATE_INTERVAL(UPDATE_INTERVAL_IN_SECONDS.value() * MILLISECONDS_PER_SECOND.value()),
    FASTEST_INTERVAL_IN_SECONDS(5),
    FASTEST_INTERVAL(FASTEST_INTERVAL_IN_SECONDS.value() * MILLISECONDS_PER_SECOND.value());

    private final int value;

    private TimeConstants(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
