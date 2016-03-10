package com.jgraycar.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ListRepresentativesActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "MqTzygecSlEiu7xB644w4OQRx";
    private static final String TWITTER_SECRET = "irdk0Nj1qxop4nYyxRcVkEo3nB2dYWu37KocA3hT6HAPRzWHU5";

    RecyclerView rv;
    private String location;
    protected static List<Senator> persons;
    private GoogleApiClient mGoogleApiClient;
    protected static final String LOCATION_KEY = "com.jgraycar.represent.location";
    protected static final String QUERY_TYPE_KEY = "com.jgraycar.represent.query_type";
    protected static final String DATA_KEY = "com.jgraycar.represent.data";
    protected static final String NAMES_KEY = "com.jgraycar.represent.names";
    protected static final String PARTIES_KEY = "com.jgraycar.represent.parties";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_representatives);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        setLocation(extras.getInt(QUERY_TYPE_KEY), extras.getString(LOCATION_KEY));
        initializeData(extras.getString(DATA_KEY));

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(this, persons);
        rv.setAdapter(adapter);
    }

    private void setLocation(int queryType, String loc) {
        if (queryType == SetLocationActivity.ZIP_CODE_LOOKUP) {
            // do stuff
            location = "Alameda County, CA";
        } else if (queryType == SetLocationActivity.CURRENT_LOCATION_LOOKUP) {
            // loc is LAT:LON
            location = "Alameda County, CA";
        }
    }

    private void initializeData(String data) {
        persons = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> parties = new ArrayList<>();

        try {
            JSONObject response = (JSONObject) new JSONTokener(data).nextValue();
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i += 1) {
                JSONObject person = results.getJSONObject(i);
                String name = person.getString("title") + ". " + person.getString("first_name") +
                        " " + person.getString("last_name");
                String email = person.getString("oc_email");
                String party = person.getString("party");
                String twitterId = person.getString("twitter_id");
                String term = person.getString("term_start").substring(0, 4) + " - " + person.getString("term_end").substring(0, 4);
                String bioguideId = person.getString("bioguide_id");
                String website = person.getString("website");
                final Senator senator = new Senator(name, term, party, email, website, twitterId, bioguideId);
                persons.add(senator);
                names.add(name);
                parties.add(party);

                RetrieveHTTPResponseTask committeesTask = new RetrieveHTTPResponseTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        ArrayList<String> committees = new ArrayList<>();

                        try {
                            JSONObject obj = (JSONObject) new JSONTokener(output).nextValue();
                            JSONArray results = obj.getJSONArray("results");

                            for (int i = 0; i < results.length(); i += 1) {
                                JSONObject committee = results.getJSONObject(i);
                                committees.add(committee.getString("name"));
                            }
                        } catch (JSONException e) {
                            Log.e("T", "Error while parsing committee JSON response");
                        }
                        senator.committees = committees.toArray(new String[0]);
                    }

                    @Override
                    public void prepareStart() {

                    }
                });

                RetrieveHTTPResponseTask billsTask = new RetrieveHTTPResponseTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        ArrayList<String> bills = new ArrayList<>();

                        try {
                            JSONObject obj = (JSONObject) new JSONTokener(output).nextValue();
                            JSONArray results = obj.getJSONArray("results");

                            for (int i = 0; i < results.length(); i += 1) {
                                JSONObject bill = results.getJSONObject(i);
                                if (!bill.getString("short_title").equals("null")) {
                                    bills.add(bill.getString("short_title"));
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("T", "Error while parsing committee JSON response");
                        }
                        senator.bills = bills.toArray(new String[0]);
                    }

                    @Override
                    public void prepareStart() {

                    }
                });

                String committeesUrl = "http://congress.api.sunlightfoundation.com/committees?member_ids=" +
                        bioguideId + "&apikey=" + SetLocationActivity.SUNLIGHT_API_KEY;
                String billsUrl = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" +
                        bioguideId + "&apikey=" + SetLocationActivity.SUNLIGHT_API_KEY;
                try {
                    committeesTask.execute(new URL(committeesUrl));
                    billsTask.execute(new URL(billsUrl));
                } catch (Exception e) {
                    Log.e("T", "Malformed URL");
                }
            }
        } catch (JSONException e) {
            // Shouldn't happen
            Log.e("T", "JSON parsing exception slipped through, uh oh");
            return;
        }

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        Bundle extras = new Bundle();

        extras.putStringArrayList(NAMES_KEY, names);
        extras.putStringArrayList(PARTIES_KEY, parties);
        extras.putString(LOCATION_KEY, location);
        sendIntent.putExtras(extras);

        startService(sendIntent);
    }

    public void goToDetailsView(View v) {
        Senator senator = (Senator) v.getTag();

        Intent intent = new Intent(this, SenatorDetailsActivity.class);
        Bundle args = new Bundle();

        args.putString(SenatorDetailsActivity.NAME_KEY, senator.name);
        args.putString(SenatorDetailsActivity.TERM_KEY, senator.term);
        args.putString(SenatorDetailsActivity.PARTY_KEY, senator.party);
        args.putInt(SenatorDetailsActivity.PHOTO_KEY, senator.photoId);

        args.putStringArray(SenatorDetailsActivity.COMMITTEES_KEY, senator.committees);
        args.putStringArray(SenatorDetailsActivity.BILLS_KEY, senator.bills);

        intent.putExtras(args);
        startActivity(intent);
    }

    public static Senator senatorWithName(String name) {
        for (Senator senator : persons) {
            if (name.equals(senator.name)) {
                return senator;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended (int cause) {
        // do nothing
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult cause) {
        // do nothing
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // shouldn't happen from this end
    }
}
