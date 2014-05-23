package io.mem0r1es.activitysubsumer.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class Utils {

    /**
     * Add new entry to set associated to the specified key, or creates new set if none existed previously
     *
     * @param map   map
     * @param key   key value
     * @param entry value of the entry to add
     * @param <K>   key's type
     * @param <V>   type of values in the list
     * @return new set containing the added entry
     */
    public static <K, V> Set<V> addToMap(Map<K, Set<V>> map, K key, V entry) {
        Set<V> list = map.get(key);
        if (list == null) {
            list = new HashSet<V>();
        }
        list.add(entry);
        map.put(key, list);
        return list;
    }
}
