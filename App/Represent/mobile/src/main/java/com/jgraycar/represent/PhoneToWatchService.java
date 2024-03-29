package com.jgraycar.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;
    private static final String SENATOR_INFO = "/SENATOR_INFO";

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();

            String[] names = extras.getStringArrayList(ListRepresentativesActivity.NAMES_KEY).toArray(new String[0]);
            String[] parties = extras.getStringArrayList(ListRepresentativesActivity.PARTIES_KEY).toArray(new String[0]);
            String location = extras.getString(ListRepresentativesActivity.LOCATION_KEY);
            double obamaVotes = extras.getDouble(ListRepresentativesActivity.OBAMA_KEY);
            double romneyVotes = extras.getDouble(ListRepresentativesActivity.ROMNEY_KEY);

            String namePart = TextUtils.join(":", names);
            String partyPart = TextUtils.join(":", parties);
            String locationPart = TextUtils.join(":", new String[] { location, String.valueOf(obamaVotes), String.valueOf(romneyVotes) });
            String[] parts = new String[]{ namePart, partyPart, locationPart };
            final String senatorInfo = TextUtils.join("+", parts);

            // Send the message with the cat name
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //first, connect to the apiclient
                    mApiClient.connect();
                    //now that you're connected, send a massage with the cat name
                    sendMessage(SENATOR_INFO, senatorInfo);
                }
            }).start();
        }

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see WatchToPhoneService for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}
