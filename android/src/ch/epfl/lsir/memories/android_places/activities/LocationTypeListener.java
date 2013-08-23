package ch.epfl.lsir.memories.android_places.activities;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;
import ch.epfl.lsir.memories.android_places.query.loc.RetrieveLocationType;
import ch.epfl.lsir.memories.android_places.utils.loc.LocationConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import java.util.concurrent.ExecutionException;

/**
 * @author Sebastian Claici
 */
public class LocationTypeListener implements LocationListener {
    private final Context context;
    private final LocationClient mLocationClient;

    public LocationTypeListener(Context context, LocationClient mLocationClient) {
        this.context = context;
        this.mLocationClient = mLocationClient;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocationType();
    }

    /**
     * Displays the location loc of the current user location in a Toast, and returns
     * a string representation.
     *
     * @return The location loc of the current location as a String.
     */
    public String getLocationType() {
        String coords = getCoordinates();
        String radius = LocationConstants.RADIUS.getValueAsString();

        String locationType = null;
        AsyncTask<String, Void, String> execute = new RetrieveLocationType(context).execute(coords, radius);
        try {
            locationType = execute.get();
        } catch (InterruptedException e) {
            showErrorDialog();
        } catch (ExecutionException e) {
            showErrorDialog();
        }

        return locationType;
    }

    /**
     * Shows an error Toast if something went wrong during the location loc retrieval.
     */
    private void showErrorDialog() {
        Toast.makeText(context, "Error with retrieving loc.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Queries the current location and returns a String representation of a latitude and longitude.
     *
     * @return A String representation of the coordinates of a point (latitude and longitude).
     */
    public String getCoordinates() {
        Location location = null;
        if (servicesConnected()) {
            location = mLocationClient.getLastLocation();
        } else {
            Toast.makeText(context, "No location found", Toast.LENGTH_LONG).show();
            return "";
        }

        return location.getLatitude() + "," + location.getLongitude();
    }

    /**
     * Checks if the Google Play services are connected.
     *
     * @return <b>true</b> if the Play services are connected, <b>false</b> otherwise
     */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.
                isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            Toast.makeText(context, "Error connecting to Google Play Services", Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }
}
