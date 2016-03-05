package com.jgraycar.represent;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisplaySenatorActivity extends Activity implements SensorEventListener {

    public ArrayList<Senator> senators;
    public int location;

    private FragmentGridPagerAdapter adapter;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float last_x;
    private float last_y;
    private float last_z;
    private long lastUpdate;
    protected static final String NAMES_KEY = "com.jgraycar.represent.names";
    protected static final String PARTIES_KEY = "com.jgraycar.represent.parties";
    protected static final String LOCATION_KEY = "com.jgraycar.represent.location";
    private static final int SHAKE_THRESHOLD = 800;

    private void initializeData(String[] names, String[] parties) {
        int[] pictures;
        if (location == 0) {
            pictures = new int[] { R.drawable.barbara, R.drawable.dianne, R.drawable.lee };
        } else if (location == 1) {
            pictures = new int[] { R.drawable.susan, R.drawable.angus, R.drawable.lisa };
        } else {
            pictures = new int[] { R.drawable.richard, R.drawable.jeff, R.drawable.mike };
        }
        senators = new ArrayList<>();

        for (int i = 0; i < names.length; i += 1) {
            senators.add(new Senator(names[i], parties[i], pictures[i]));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        senators = new ArrayList<>();
        String[] names = new String[] {};
        String[] parties = new String[] {};

        if (extras != null) {
            names = extras.getStringArray(NAMES_KEY);
            parties = extras.getStringArray(PARTIES_KEY);
            location = extras.getInt(LOCATION_KEY);
        }

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        adapter = new SenatorGridPagerAdapter(this, getFragmentManager());
        pager.setAdapter(adapter);

        initializeData(names, parties);

        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Shake detection code courtesy of N-JOY (http://stackoverflow.com/a/5271532)
        float[] values = event.values;
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = values[0];
            float y = values[1];
            float z = values[2];

            float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                System.out.println("Shake detected!");
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("path", "/randomize_location");
                startService(sendIntent);
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int value) {
        // do nothing
    }
}
