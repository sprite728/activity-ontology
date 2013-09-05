package ch.epfl.lsir.memories.android_places.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.example.Places_API.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import ch.epfl.lsir.memories.android_places.utils.TimeConstants;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;
    private boolean mUpdatesRequested;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPrefs;

    private void setupLocationFeatures() {
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
        // Create new location listener
        mLocationListener = new LocationTypeListener(this, mLocationClient);
        // Start with updates turned on
        mUpdatesRequested = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLocationFeatures();
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
            mLocationClient.removeLocationUpdates(mLocationListener);
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
        mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public void benchmark(View view) {
        Intent intent = new Intent(this, BenchmarkActivity.class);
        startActivity(intent);
    }

}
