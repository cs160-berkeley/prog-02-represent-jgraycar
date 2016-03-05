package com.jgraycar.represent;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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

public class DisplaySenatorActivity extends Activity {

    public ArrayList<Senator> senators;
    public int location;

    private FragmentGridPagerAdapter adapter;
    protected static final String NAMES_KEY = "com.jgraycar.represent.names";
    protected static final String PARTIES_KEY = "com.jgraycar.represent.parties";
    protected static final String LOCATION_KEY = "com.jgraycar.represent.location";

    private void initializeData(String[] names, String[] parties) {
        int[] pictures;
        if (location == 0) {
            pictures = new int[] { R.drawable.barbara, R.drawable.dianne, R.drawable.lee };
        } else {
            pictures = new int[] { R.drawable.susan, R.drawable.angus, R.drawable.lisa };
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

}
