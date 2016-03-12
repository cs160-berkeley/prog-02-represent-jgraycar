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

import java.nio.charset.StandardCharsets;
import java.util.List;

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
            Senator senator = ListRepresentativesActivity.senatorWithName(name);

            Intent intent = new Intent(this, SenatorDetailsActivity.class);
            Bundle args = new Bundle();

            args.putString(SenatorDetailsActivity.NAME_KEY, senator.name);
            args.putString(SenatorDetailsActivity.TERM_KEY, senator.term);
            args.putString(SenatorDetailsActivity.PARTY_KEY, senator.party);
            args.putStringArray(SenatorDetailsActivity.COMMITTEES_KEY, senator.committees);
            args.putStringArray(SenatorDetailsActivity.BILLS_KEY, senator.bills);

            intent.putExtras(args);
            //you need to add this flag since you're starting a new activity from a service
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase(SHAKE_PATH)) {
            System.out.println("watch shaken!");
            Intent intent = new Intent(this, ListRepresentativesActivity.class);
            intent.putExtra(ListRepresentativesActivity.LOCATION_KEY, 2);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }

    }
}
