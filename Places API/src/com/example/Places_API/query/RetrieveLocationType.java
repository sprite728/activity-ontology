package com.example.Places_API.query;

import android.content.Context;
import android.os.AsyncTask;
import com.example.Places_API.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
 * Retrieve the location type by making a request to the Google Places API.
 *
 * As the API can return multiple types, the location type with the most occurrences is returned.
 *
 * @author Sebastian Claici
 */
public final class RetrieveLocationType extends AsyncTask<String, Void, String> {

    private static final String key = "{API_KEY}";
    private static final String url = "https://maps.googleapis.com/maps/api/place/search/json";

    private final Context context;
    private Exception exception;

    public RetrieveLocationType(Context context) {
        this.context = context;
    }

    /**
     * Query the Google Places API for a location type given GPS coordinates and a radius.
     *
     * @param params Parameters required are:
     *                  * location: GPS coordinates of the location, represented as a String; e.g., "12.223,32.334"
     *                  * radius: Radius for which to return an answer, expressed in meters
     * @return A String containing the type of the location
     */
    @Override
    protected String doInBackground(String... params) {
        String retval = null;
        try {
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
            List<String> locationTypes = Utils.getLocationTypes(context);
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

            retval = types.get(0);
        } catch (Exception e) {
            exception = e;
            return null;
        }

        return retval;
    }
}
