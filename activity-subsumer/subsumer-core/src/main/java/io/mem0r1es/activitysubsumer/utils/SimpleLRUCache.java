package io.mem0r1es.activitysubsumer.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Last recently used type of cache.
 * @author Ivan Gavrilović
 */
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    public SimpleLRUCache(int capacity) {
        // do not increase, and keep the ordering in access order
        super(capacity, 1, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
