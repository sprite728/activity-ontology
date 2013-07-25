package com.example.Places_API.activities;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.example.Places_API.R;
import com.example.Places_API.query.RetrieveLocationType;
import com.example.Places_API.utils.LocationConstants;
import com.example.Places_API.utils.TimeConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private boolean mUpdatesRequested;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and setup a location request
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(TimeConstants.UPDATE_INTERVAL.value());
        mLocationRequest.setFastestInterval(TimeConstants.FASTEST_INTERVAL.value());

        // Create Editor for shared preferences
        mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();

        // Create new location client
        mLocationClient = new LocationClient(this, this, this);
        // Start with updates turned on
        mUpdatesRequested = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        if (mLocationClient.isConnected()) {
            // Remove location updates for client
            mLocationClient.removeLocationUpdates(this);
        }

        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        // Save the current settings for updates
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        // Get previous setting for location updates
        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested =
                    mPrefs.getBoolean("KEY_UPDATES_ON", false);
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }

        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        // Request location updates
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocationType();
    }

    /**
     * Displays the location type of the current user location in a Toast, and returns
     * a string representation.
     *
     * @return The location type of the current location as a String.
     */
    public String getLocationType() {
        String coords = getCoordinates();
        String radius = LocationConstants.RADIUS.getValueAsString();

        String locationType = null;
        AsyncTask<String,Void,String> execute = new RetrieveLocationType(this).execute(coords, radius);
        try {
            locationType = execute.get();
            Toast.makeText(this, locationType, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            showErrorDialog();
        } catch (ExecutionException e) {
            showErrorDialog();
        }

        return locationType;
    }

    /**
     * Shows an error Toast if something went wrong during the location type retrieval.
     */
    private void showErrorDialog() {
        Toast.makeText(this, "Error with retrieving type.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "No location found", Toast.LENGTH_LONG).show();
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
                            isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();

            return false;
        }

        return true;
    }

}
