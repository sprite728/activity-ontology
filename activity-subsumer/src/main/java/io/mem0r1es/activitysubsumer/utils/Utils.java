package io.mem0r1es.activitysubsumer.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
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

    public static int parseMinutes(String input){
        int cnt = 0, i = 0;
        char curr = input.charAt(i);

        while (Character.isDigit(curr)) {
            cnt = cnt * 10 + Integer.parseInt(Character.toString(curr));
            i++;
            curr = input.charAt(i);
        }
        if (curr == 'h') {
            cnt = 60 * cnt;
        }
        return cnt;
    }

    /**
     * Splits the input using space as delimiter, and decodes individual parts by using {@link java.net.URLDecoder}
     * @param input input to decode
     * @return array containing the decoded input components
     */
    public static String[] decodeParts(String input) {
        String parts[] = input.split("\\s");
        for (int i = 0; i < parts.length; i++) {
            try {
                parts[i] = URLDecoder.decode(parts[i], "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parts;
    }

    /**
     * Concatenates the elements of the list with space as delimiter. It encoded the {@link java.net.URLEncoder} to individual parts before contatenation
     * @param parts parts to concatenate
     * @return string containing the concatenated, encoded string
     */
    public static String encodeParts(List<String> parts) {
        String output = "";
        try {
            for (String s : parts) {
                output += URLEncoder.encode(s, "UTF-8") + " ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
