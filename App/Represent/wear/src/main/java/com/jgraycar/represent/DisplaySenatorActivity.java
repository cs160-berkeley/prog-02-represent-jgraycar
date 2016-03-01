package com.jgraycar.represent;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.widget.TextView;

public class DisplaySenatorActivity extends Activity {
    public Senator[] senators;

    private void initializeData() {
        senators = new Senator[] { new Senator("Senator Barbara Boxer", "Democrat", R.drawable.barbara),
                new Senator("Senator Mitch McConnell", "Republican", R.drawable.mitch),
                new Senator("Senator Bernie Sanders", "Independent", R.drawable.bernie) };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        initializeData();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String catName = extras.getString("CAT_NAME");
        }

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new SenatorGridPagerAdapter(this, getFragmentManager()));

        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(pager);
    }
}
