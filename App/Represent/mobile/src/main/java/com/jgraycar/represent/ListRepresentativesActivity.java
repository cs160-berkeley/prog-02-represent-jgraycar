package com.jgraycar.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
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
    private static final String GOOGLE_API_KEY = "AIzaSyCKezGv9d4dGgw7WgKSFbRSYmUlm4M6Yfw";

    private String state;
    private String county;
    private boolean personsLoaded;
    private boolean sentToWatch;
    private double obamaVote;
    private double romneyVote;
    protected ImageLoader imageLoader;
    protected static List<Senator> persons;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView rv;
    private ProgressBar progressBar;

    protected static final String LOCATION_KEY = "com.jgraycar.represent.location";
    protected static final String QUERY_TYPE_KEY = "com.jgraycar.represent.query_type";
    protected static final String DATA_KEY = "com.jgraycar.represent.data";
    protected static final String NAMES_KEY = "com.jgraycar.represent.names";
    protected static final String PARTIES_KEY = "com.jgraycar.represent.parties";
    protected static final String OBAMA_KEY = "com.jgraycar.represent.obama";
    protected static final String ROMNEY_KEY = "com.jgraycar.represent.romney";

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        personsLoaded = false;
        sentToWatch = false;
        obamaVote = -1;
        romneyVote = -1;

        setLocation(extras.getInt(QUERY_TYPE_KEY), extras.getString(LOCATION_KEY));
        initializeData(extras.getString(DATA_KEY));

    }

    private void setLocation(int queryType, String loc) {
        if (queryType == SetLocationActivity.ZIP_CODE_LOOKUP) {
            // Convert zipcode to coordinates and call again
            String url = "https://maps.googleapis.com/maps/api/geocode/json?&address=" + loc +
                    "&key=" + GOOGLE_API_KEY;
            Log.d("Location", "Zip code query to " + url);
            RetrieveHTTPResponseTask task = new RetrieveHTTPResponseTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONObject obj = (JSONObject) new JSONTokener(output).nextValue();
                        JSONObject result = obj.getJSONArray("results").getJSONObject(0);
                        JSONObject geo = result.getJSONObject("geometry");
                        JSONObject loc = geo.getJSONObject("location");
                        double lat = loc.getDouble("lat");
                        double lng = loc.getDouble("lng");
                        String coords = String.valueOf(lat) + ":" + String.valueOf(lng);
                        setLocation(SetLocationActivity.CURRENT_LOCATION_LOOKUP, coords);
                    } catch (JSONException e) {
                        Log.e("Location", "Error while parsing JSON: " + e.getLocalizedMessage());
                    }
                }

                @Override
                public void prepareStart() {

                }
            });

            try {
                task.execute(new URL(url));
            } catch (MalformedURLException e) { }
            return;
        }

        // loc is LAT:LON
        String[] parts = loc.split(":");
        String lat = parts[0];
        String lng = parts[1];

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng +
                "&key=" + GOOGLE_API_KEY;
        Log.d("Location", "Lookup URL: " + url);
        RetrieveHTTPResponseTask task = new RetrieveHTTPResponseTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(output).nextValue();
                    JSONArray results = obj.getJSONArray("results");

                    for (int i = 0; i < results.length(); i += 1) {
                        JSONObject result = results.getJSONObject(i);
                        JSONArray types = result.getJSONArray("types");
                        boolean isPostalCode = false;

                        for (int j = 0; j < types.length(); j += 1) {
                            String type = types.getString(j);
                            if (type.equals("administrative_area_level_2")) {
                                isPostalCode = true;
                                break;
                            }
                        }

                        if (!isPostalCode) {
                            continue;
                        }

                        JSONArray addressComponents = result.getJSONArray("address_components");
                        for (int k = 0; k < addressComponents.length(); k += 1) {
                            JSONObject component = addressComponents.getJSONObject(k);
                            JSONArray componentTypes = component.getJSONArray("types");
                            for (int m = 0; m < componentTypes.length(); m += 1) {
                                String componentType = componentTypes.getString(m);
                                if (componentType.equals("administrative_area_level_2")) {
                                    county = component.getString("short_name");
                                    break;
                                } else if (componentType.equals("administrative_area_level_1")) {
                                    state = component.getString("short_name");
                                    break;
                                }
                            }
                        }
                        break;
                    }
                } catch (JSONException e) {
                    Log.e("Location", "Error while parsing JSON response");
                }
                Log.d("Location", "Location detected to be " + county + ", " + state);
                getElectionVotes();
            }

            @Override
            public void prepareStart() { }
        });

        try {
            task.execute(new URL(url));
        } catch (MalformedURLException e) { }
    }

    private void getElectionVotes() {
        if (county == null) {
            return;
        }

        String url = "https://raw.githubusercontent.com/cs160-sp16/voting-data/master/election-county-2012.json";
        RetrieveHTTPResponseTask task = new RetrieveHTTPResponseTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONArray results = (JSONArray) new JSONTokener(output).nextValue();

                    for (int i = 0; i < results.length(); i += 1) {
                        JSONObject obj = results.getJSONObject(i);
                        if (obj.getString("state-postal").equals(state) &&
                                county.startsWith(obj.getString("county-name"))) {
                            obamaVote = obj.getDouble("obama-percentage");
                            romneyVote = obj.getDouble("romney-percentage");
                            break;
                        }
                    }
                    sendToWatch();
                } catch (JSONException e) {

                }
            }

            @Override
            public void prepareStart() {

            }
        });

        try {
            task.execute(new URL(url));
        } catch (MalformedURLException e) { }
    }

    private void initializeData(String data) {
        persons = new ArrayList<>();

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
                String website = person.getString("website").replace("http://", "").replace("https://", "");
                final Senator senator = new Senator(name, term, party, email, website, twitterId, bioguideId);
                persons.add(senator);

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
                                    String date = bill.getString("introduced_on");
                                    String[] parts = date.split("-");
                                    date = parts[1] + "/" + parts[2] + "/" + parts[0];
                                    String text = bill.getString("short_title") + " (" + date + ")";
                                    bills.add(text);
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

        personsLoaded = true;
        getTwitterInfo();
        sendToWatch();
    }

    private void sendToWatch() {
        if (personsLoaded && obamaVote >= 0 && romneyVote >= 0 && !sentToWatch) {
            sentToWatch = true;
            Log.d("Watch", "Sending to watch");
            Log.d("Watch", "Obama: " + String.valueOf(obamaVote));
            Log.d("Watch", "Romney: " + String.valueOf(romneyVote));
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
            extras.putString(LOCATION_KEY, county + ", " + state);
            extras.putDouble(OBAMA_KEY, obamaVote);
            extras.putDouble(ROMNEY_KEY, romneyVote);
            sendIntent.putExtras(extras);

            startService(sendIntent);
        }
    }

    public void getTwitterInfo() {
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> result) {
                MyTwitterApiClient client = new MyTwitterApiClient(result.data);
                for (final Senator person : persons) {
                    client.getUserDataService().show(person.twitterId, new Callback<User>() {
                        @Override
                        public void success(Result<User> result) {
                            User user = result.data;
                            Log.d("T", "Twitter profile pic: " + user.profileImageUrl);
                            person.photoUrl = result.data.profileImageUrl.replace("_normal", "");
                            imageLoader.loadImage(person.photoUrl, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    person.photo = loadedImage;

                                    if (readyToConstructCards()) {
                                        constructCards();
                                    }
                                }

                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            Log.e("T", "Error while retrieving twitter profile picture: " + e.getMessage());
                        }
                    });

                    client.getStatusesService().userTimeline(null, person.twitterId, 1, null, null,
                            null, null, null, null, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> result) {
                            if (result.data.size() > 0) {
                                Tweet tweet = result.data.get(0);
                                person.tweetId = tweet.id;
                                Log.d("T", "Tweet id: " + String.valueOf(tweet.id));
                                person.tweetView = new TweetView(ListRepresentativesActivity.this, tweet);

                                if (readyToConstructCards()) {
                                    constructCards();
                                }
                            } else {
                                person.tweetId = -1;
                            }
                        }

                        @Override
                        public void failure(TwitterException e) {
                            Log.e("T", "Error while retrieving last tweet: " + e.getMessage());
                            person.tweetId = -1;
                        }
                    });
                }
            }

            @Override
            public void failure(TwitterException e) {
                constructCards();
            }
        });
    }

    private boolean readyToConstructCards() {
        for (Senator senator : persons) {
            if (senator.photo == null || senator.tweetId == 0) {
                return false;
            }
        }

        return true;
    }

    private void constructCards() {
        Log.d("T", "Constructing cards");
        if (rv == null) {
            rv = (RecyclerView)findViewById(R.id.rv);
            rv.setVisibility(View.GONE);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            RVAdapter adapter = new RVAdapter(this, persons);
            rv.setAdapter(adapter);

            rv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void goToDetailsView(View v) {
        Senator senator = (Senator) v.getTag();

        Intent intent = new Intent(this, SenatorDetailsActivity.class);
        Bundle args = new Bundle();

        args.putString(SenatorDetailsActivity.NAME_KEY, senator.name);
        args.putString(SenatorDetailsActivity.TERM_KEY, senator.term);
        args.putString(SenatorDetailsActivity.PARTY_KEY, senator.party);

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
