package ch.epfl.lsir.memories.android_places.utils;

/**
 * Various integer constants. Will remove if we don't need it for very many of them.
 *
 * @author Sebastian Claici
 */
public enum LocationConstants {
    RADIUS(100);

    private final int value;

    private LocationConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getValueAsString() {
        return String.valueOf(value);
    }
}
