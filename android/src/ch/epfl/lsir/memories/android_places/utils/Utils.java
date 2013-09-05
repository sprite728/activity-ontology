package ch.epfl.lsir.memories.android_places.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * General purpose utilities.
 *
 * @author Sebastian Claici
 */
public final class Utils {

    private Utils() {}

    /**
     * Find the extension of a file. Note that this is not meant to be a comprehensive solution, as it only
     * takes the last letters after a dot into account.
     *
     * @param file File for which to find the extension.
     * @return The extension of the file, as a String.
     */
    public static String getExtension(File file) {
        String name = file.getName();

        return name.substring(name.lastIndexOf("."));
    }

    public static String join(String sep, String... args) {
        if (args.length == 0)
            return "";

        StringBuffer buf = new StringBuffer(args[0]);
        for (int i = 1; i < args.length; ++i) {
            buf.append(sep);
            buf.append(args[i]);
        }

        return buf.toString();
    }

    /**
     * Read all the lines from a file, and return them as a String array.
     *
     * @param context Android context used to retrieve resources
     * @param is InputStream from which to read
     * @return an array of Strings representing the lines of the file
     */
    public static String[] readLines(Context context, InputStream is) {
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        List<String> result = new ArrayList<String>();
        try {
            for (String line = buf.readLine(); line != null; line = buf.readLine())
                result.add(line);
        } catch (IOException ioe) {
            return new String[0];
        }

        return result.toArray(new String[0]);
    }
}
