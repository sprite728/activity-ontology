package ch.epfl.lsir.memories.android_places.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import ch.epfl.lsir.memories.android_places.utils.TimeConstants;
import ch.epfl.lsir.memories.android_places.utils.Utils;
import com.example.Places_API.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.InputStream;
import java.util.Arrays;

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

    private void setupAutoComplete() {
        // Create and setup the two autocomplete forms
        AutoCompleteTextView verbComplete = (AutoCompleteTextView) findViewById(R.id.verbs);
        AutoCompleteTextView objectComplete = (AutoCompleteTextView) findViewById(R.id.objects);

        String[] verbs = Utils.readLines(this, getResources().openRawResource(R.raw.verbs));
        String[] objects = Utils.readLines(this, getResources().openRawResource(R.raw.objects));
        ArrayAdapter<String> verbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, verbs);
        ArrayAdapter<String> objectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                objects);

        // Word validator that checks whether the user string is in the array of verbs/objects
        class WordValidator implements AutoCompleteTextView.Validator {
            private final String[] array;

            public WordValidator(String[] array) {
                this.array = array;
            }

            @Override
            public boolean isValid(CharSequence text) {
                String val = text.toString().toLowerCase();

                return Arrays.binarySearch(array, val) != -1;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return invalidText.subSequence(0, 0);
            }
        }
        verbComplete.setValidator(new WordValidator(verbs));
        objectComplete.setValidator(new WordValidator(objects));

        // Start to autocomplete after the user has typed two characters
        verbComplete.setThreshold(2);
        objectComplete.setThreshold(2);
        verbComplete.setAdapter(verbAdapter);
        objectComplete.setAdapter(objectAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLocationFeatures();
        setupAutoComplete();
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
