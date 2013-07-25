package com.example.Places_API.utils;

import android.content.Context;
import com.example.Places_API.R;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Various utility functions.
 *
 * @author Sebastian Claici
 */
public final class Utils {

    private static final String key = "{API KEY}";
    private static final String url = "https://maps.googleapis.com/maps/api/place/search/json";

    private static List<String> locationTypes;

    private Utils() {}

    /**
     * Initialize the locationTypes array from a static resource file.
     *
     * @param buf BufferedReader from which to read the locations
     */
    public static void initLocationTypes(BufferedReader buf) {
        if (locationTypes != null && !locationTypes.isEmpty())
            return;

        locationTypes = new ArrayList<String>();

        try {
            for (String line = buf.readLine(); line != null; line = buf.readLine()) {
                locationTypes.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch the locationTypes array, initializing it if it was null.
     *
     * @return A list of Strings representing all the valid location types.
     */
    private static List<String> getLocationTypes() {
        return locationTypes;
    }

    public static String getLocationType(String[] params)
            throws IOException, ParseException {
        String charset = "UTF-8";
        String query = String.format("location=%s&radius=%s&sensor=false&key=%s",
                URLEncoder.encode(params[0], charset),
                URLEncoder.encode(params[1], charset),
                URLEncoder.encode(key, charset));

        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);

        InputStream response = connection.getInputStream();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(new InputStreamReader(response));
        JSONArray results = (JSONArray) object.get("results");

        final Map<String, Integer> counter = new HashMap<String, Integer>();
        for (Object result : results) {
            JSONObject place = (JSONObject) result;
            JSONArray types = (JSONArray) place.get("types");

            for (Object type : types) {
                String typeStr = (String) type;
                if (locationTypes.contains(typeStr) && !typeStr.equals("establishment")) {
                    if (counter.containsKey(typeStr)) {
                        counter.put(typeStr, counter.get(typeStr) + 1);
                    } else {
                        counter.put(typeStr, 1);
                    }
                }
            }
        }

        List<String> types = new ArrayList<String>(counter.keySet());
        Collections.sort(types, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return -(counter.get(lhs) - counter.get(rhs));
            }
        });

        if (types.isEmpty())
            return "unknown";

        return types.get(0);
    }
}
