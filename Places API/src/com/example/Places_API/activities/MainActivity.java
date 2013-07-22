package com.example.Places_API.activities;

import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.example.Places_API.R;
import com.example.Places_API.query.RetrieveLocationType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    // Test code right now; should flash "Food"
    public void getLocation(View view) {

        String url = "https://maps.googleapis.com/maps/api/place/search/json";
        String location = "46.520293,6.629902";
        String radius = "100";

        try {
            AsyncTask<String,Void,String> execute = new RetrieveLocationType(this).execute(url, location, radius);
            Toast.makeText(this, execute.get(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
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
