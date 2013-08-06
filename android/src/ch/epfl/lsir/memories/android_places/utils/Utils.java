package ch.epfl.lsir.memories.android_places.utils;

import java.io.File;

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
}
