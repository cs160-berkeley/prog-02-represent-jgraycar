package com.jgraycar.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class ListRepresentativesActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    RecyclerView rv;
    private int location;
    protected static List<Senator> persons;
    private GoogleApiClient mGoogleApiClient;
    protected static final String LOCATION_KEY = "com.jgraycar.represent.location";
    protected static final String NAMES_KEY = "com.jgraycar.represent.names";
    protected static final String PARTIES_KEY = "com.jgraycar.represent.parties";

    private void initializeData() {
        persons = new ArrayList<>();
        String[] bills = getResources().getStringArray(R.array.bills_array);
        String[] committees = getResources().getStringArray(R.array.committees_array);

        if (location == 0) {
            // Congress people for Alameda County, California
            persons.add(new Senator("Sen. Barbara Boxer", "1993 - 2016", R.drawable.barbara,
                    "Democrat", "senator@boxer.senate.gov", "www.boxer.senate.gov",
                    getResources().getString(R.string.barb_tweet), committees, bills));
            persons.add(new Senator("Sen. Dianne Feinstein", "1992 - 2016", R.drawable.dianne,
                    "Democrat", "senator@feinstein.senate.gov", "www.feinstein.senate.gov",
                    getResources().getString(R.string.dianne_tweet), committees, bills));
            persons.add(new Senator("Rep. Barbara Lee", "2013 - 2016", R.drawable.lee,
                    "Democrat", "lee@house.gov", "lee.house.gov",
                    getResources().getString(R.string.lee_tweet), committees, bills));
        } else if (location == 1){
            // Congress people for Gorham, Maine
            persons.add(new Senator("Sen. Susan Collins", "1997 - 2016", R.drawable.susan,
                    "Republican", "senator@collins.senate.gov", "www.collins.senate.gov",
                    getResources().getString(R.string.susan_tweet), committees, bills));
            persons.add(new Senator("Sen. Angus King", "2013 - 2016", R.drawable.angus,
                    "Independent", "senator@king.senate.gov", "www.king.senate.gov",
                    getResources().getString(R.string.angus_tweet), committees, bills));
            persons.add(new Senator("Rep. Linda Sanborn", "2008 - 2016", R.drawable.lisa,
                    "Democrat", "linda.sanborn@legislature.maine.gov",
                    "legislature.maine.gov/housedems/sanbornl/index.html",
                    getResources().getString(R.string.lisa_tweet), committees, bills));
        } else {
            // Congress people for Calhoun, Alabama
            persons.add(new Senator("Sen. Richard Shelby", "1987 - 2016", R.drawable.richard,
                    "Republican", "senator@shelby.senate.gov", "www.shelby.senate.gov",
                    getResources().getString(R.string.richard_tweet), committees, bills));
            persons.add(new Senator("Sen. Jeff Sessions", "1997 - 2016", R.drawable.jeff,
                    "Republican", "senator@session.senate.gov", "www.sessions.senate.gov",
                    getResources().getString(R.string.jeff_tweet), committees, bills));
            persons.add(new Senator("Rep. Mike Rogers", "2003 - 2016", R.drawable.mike,
                    "Republican", "mikerogers@house.gov", "mikerogers.house.gov",
                    getResources().getString(R.string.mike_tweet), committees, bills));
        }

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> parties = new ArrayList<>();

        for (Senator senator : persons) {
            names.add(senator.name);
            parties.add(senator.party);
        }

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        Bundle extras = new Bundle();

        extras.putStringArrayList(NAMES_KEY, names);
        extras.putStringArrayList(PARTIES_KEY, parties);
        extras.putInt(LOCATION_KEY, location);
        sendIntent.putExtras(extras);

        startService(sendIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_representatives);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            location = extras.getInt(LOCATION_KEY);
        } else {
            location = 0;
        }

        initializeData();

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
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
