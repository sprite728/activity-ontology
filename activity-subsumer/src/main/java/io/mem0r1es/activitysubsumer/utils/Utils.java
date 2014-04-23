package io.mem0r1es.activitysubsumer.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Gavrilović
 */
public class Utils {

    /**
     * Add new entry to list associated to the specified key, or creates new list if none existed previously
     * @param map map
     * @param key key value
     * @param entry value of the entry to add
     * @param <K> key's type
     * @param <V> type of values in the list
     * @return new list containing the added entry
     */
    public static <K, V> List<V> addToMap(Map<K,List<V>> map, K key, V entry){
        List<V> list = map.get(key);
        if (list == null){
            list = new LinkedList<V>();
        }
        list.add(entry);
        map.put(key, list);
        return list;
    }
}
