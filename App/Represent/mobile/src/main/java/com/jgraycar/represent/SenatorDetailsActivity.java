package com.jgraycar.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SenatorDetailsActivity extends AppCompatActivity {
    protected static final String NAME_KEY = "com.jgraycar.represent.name";
    protected static final String TERM_KEY = "com.jgraycar.represent.term";
    protected static final String PHOTO_KEY = "com.jgraycar.represent.photo_id";
    protected static final String PARTY_KEY = "com.jgraycar.represent.party";
    protected static final String COMMITTEES_KEY = "com.jgraycar.represent.committees";
    protected static final String BILLS_KEY = "com.jgraycar.represent.bills";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senator_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        String name = args.getString(NAME_KEY);
        String term = args.getString(TERM_KEY);
        int photoId = args.getInt(PHOTO_KEY);
        String party = args.getString(PARTY_KEY);
        String[] committees = args.getStringArray(COMMITTEES_KEY);
        String[] bills = args.getStringArray(BILLS_KEY);

        int partyIconId;

        switch (party) {
            case "Democrat":
                partyIconId = R.drawable.democrat_icon;
                break;
            case "Republican":
                partyIconId = R.drawable.republican_icon;
                break;
            default:
                partyIconId = R.drawable.independent_icon;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(partyIconId);
    }
}
