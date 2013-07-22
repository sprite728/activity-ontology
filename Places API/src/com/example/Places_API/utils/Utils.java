package com.example.Places_API.utils;

import android.content.Context;
import com.example.Places_API.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Various utility functions.
 *
 * @author Sebastian Claici
 */
public final class Utils {
    private static List<String> locationTypes;

    private Utils() {}

    /**
     * Initialize the locationTypes array from a static resource file.
     *
     * @param context Context of the Android application.
     */
    public static void initLocationTypes(Context context) {
        locationTypes = new ArrayList<String>();

        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(R.raw.locations)));
            for (String line = buf.readLine(); line != null; line = buf.readLine()) {
                locationTypes.add(line.trim());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Fetch the locationTypes array, initializing it if it was null.
     *
     * @param context Context of the Android application.
     * @return A list of Strings representing all the valid location types.
     */
    public static List<String> getLocationTypes(Context context) {
        if (locationTypes == null)
            initLocationTypes(context);

        return locationTypes;
    }
}
