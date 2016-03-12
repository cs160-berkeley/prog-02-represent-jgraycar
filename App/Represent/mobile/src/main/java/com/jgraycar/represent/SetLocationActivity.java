package com.jgraycar.represent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;

public class SetLocationActivity extends AppCompatActivity implements AsyncResponse,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String SUNLIGHT_API_KEY = "9f1c202821644debb61d3096ee4c5c9c";
    protected static final int ZIP_CODE_LOOKUP = 1;
    protected static final int CURRENT_LOCATION_LOOKUP = 2;
    private int lookupType = 0;
    private String location = "";
    private ProgressBar progressBar;
    private LinearLayout locationEntry;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        locationEntry = (LinearLayout) findViewById(R.id.locationEntry);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void useZipCode(View view) {
        lookupType = ZIP_CODE_LOOKUP;
        TextView zipCodeEditText = (TextView) findViewById(R.id.zipCodeEditText);
        location = zipCodeEditText.getText().toString();
        Log.d("T", "searching with zip code " + location);

        String urlStr = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + location
                + "&apikey=" + SUNLIGHT_API_KEY;
        try {
            URL url = new URL(urlStr);
            new RetrieveHTTPResponseTask(this).execute(url);
        } catch (Exception e) {
            // do something
            Log.e("T", "error while forming URL");

            progressBar.setVisibility(View.GONE);
            locationEntry.setVisibility(View.VISIBLE);
        }
    }

    public void useCurrentLocation(View view) {
        if (mLastLocation != null) {
            lookupType = CURRENT_LOCATION_LOOKUP;
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            location = String.valueOf(latitude) + ":" + String.valueOf(longitude);
            Log.d("T", "searching with current location " + location);

            String urlStr = "http://congress.api.sunlightfoundation.com/legislators/locate?latitude=" +
                    String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude) + "&apikey=" + SUNLIGHT_API_KEY;
            try {
                URL url = new URL(urlStr);
                new RetrieveHTTPResponseTask(this).execute(url);
            } catch (Exception e) {
                // do something
                Log.e("T", "error while forming URL");

                progressBar.setVisibility(View.GONE);
                locationEntry.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d("T", "Last location is null");
            showDialog("Location could not be detected");

            progressBar.setVisibility(View.GONE);
            locationEntry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void processFinish (String response){
        if (response == null) {
            // Do something
            Log.d("T", "NULL RESPONSE");
            showDialog("Error connecting to server");

            progressBar.setVisibility(View.GONE);
            locationEntry.setVisibility(View.VISIBLE);
            return;
        }


        int numResults;
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            numResults = object.getInt("count");
        } catch (JSONException e) {
            Log.e("T", "Malformed JSON response");
            showDialog("Error while parsing results");

            progressBar.setVisibility(View.GONE);
            locationEntry.setVisibility(View.VISIBLE);
            return;
        }

        if (numResults == 0) {
            showDialog("No representatives could be found for the desired location");

            progressBar.setVisibility(View.GONE);
            locationEntry.setVisibility(View.VISIBLE);
            return;
        }

        Intent intent = new Intent(this, ListRepresentativesActivity.class);
        intent.putExtra(ListRepresentativesActivity.DATA_KEY, response);
        intent.putExtra(ListRepresentativesActivity.LOCATION_KEY, location);
        intent.putExtra(ListRepresentativesActivity.QUERY_TYPE_KEY, lookupType);
        startActivity(intent);

        progressBar.setVisibility(View.GONE);
        locationEntry.setVisibility(View.VISIBLE);
    }

    @Override
    public void prepareStart() {
        locationEntry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showDialog(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult cause) {
        mGoogleApiClient.disconnect();
    }
}
