package ch.epfl.lsir.memories.android_places.query;

import android.content.Context;
import android.os.AsyncTask;
import com.example.Places_API.R;
import ch.epfl.lsir.memories.android_places.utils.LocationUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Retrieve the location type by making a request to the Google Places API.
 * <p/>
 * As the API can return multiple types, the location type with the most occurrences is returned.
 *
 * @author Sebastian Claici
 */
public final class RetrieveLocationType extends AsyncTask<String, Void, String> {

    private final Context context;
    private Exception exception;

    public RetrieveLocationType(Context context) {
        this.context = context;
    }

    /**
     * Query the Google Places API for a location type given GPS coordinates and a radius.
     *
     * @param params Parameters required are:
     *               * location: GPS coordinates of the location, represented as a String; e.g., "12.223,32.334"
     *               * radius: Radius for which to return an answer, expressed in meters
     * @return A String containing the type of the location
     */
    @Override
    protected String doInBackground(String... params) {
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.locations)));
        LocationUtils.initLocationTypes(buf);

        String retval = null;
        try {
            retval = LocationUtils.getLocationType(params);
        } catch (Exception e) {
            exception = e;
            return null;
        }

        return retval;
    }

}
