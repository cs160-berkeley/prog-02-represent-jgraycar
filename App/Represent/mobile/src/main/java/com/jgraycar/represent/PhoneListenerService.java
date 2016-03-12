package com.jgraycar.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String DETAILS_PATH = "/show_details";
    private static final String SHAKE_PATH = "/randomize_location";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(DETAILS_PATH)) {

            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "received name: " + name);

            Intent intent = new Intent(this, SenatorDetailsActivity.class);
            Bundle args = new Bundle();

            args.putString(SenatorDetailsActivity.NAME_KEY, name);

            intent.putExtras(args);
            //you need to add this flag since you're starting a new activity from a service
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase(SHAKE_PATH)) {
            final String location = randomZip();

            String urlStr = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + location
                    + "&apikey=" + SetLocationActivity.SUNLIGHT_API_KEY;
            RetrieveHTTPResponseTask task = new RetrieveHTTPResponseTask(new AsyncResponse() {
                @Override
                public void processFinish(String response) {
                    if (response == null) {
                        return;
                    }


                    int numResults;
                    try {
                        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                        numResults = object.getInt("count");
                    } catch (JSONException e) {
                        Log.e("T", "Malformed JSON response");
                        return;
                    }

                    if (numResults == 0) {
                        return;
                    }

                    Intent intent = new Intent(PhoneListenerService.this, ListRepresentativesActivity.class);
                    intent.putExtra(ListRepresentativesActivity.DATA_KEY, response);
                    intent.putExtra(ListRepresentativesActivity.LOCATION_KEY, location);
                    intent.putExtra(ListRepresentativesActivity.QUERY_TYPE_KEY, SetLocationActivity.ZIP_CODE_LOOKUP);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }

                @Override
                public void prepareStart() {

                }
            });

            try {
                URL url = new URL(urlStr);
                task.execute(url);
            } catch (Exception e) {
            }
            System.out.println("watch shaken!");
        } else {
            super.onMessageReceived(messageEvent);
        }

    }

    private String randomZip() {
        Random rand = new Random();
        return String.valueOf(rand.nextInt(10000));
    }
}
