package com.jgraycar.represent;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SenatorDetailsActivity extends AppCompatActivity {
    protected static final String NAME_KEY = "com.jgraycar.represent.name";
    protected static final String TERM_KEY = "com.jgraycar.represent.term";
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
        Senator senator = ListRepresentativesActivity.senatorWithName(name);
        String term = senator.term;
        String party = senator.party;
        String[] committees = senator.committees;
        String[] bills = senator.bills;

        int partyIconId;

        switch (party) {
            case "D":
                partyIconId = R.drawable.democrat_icon;
                break;
            case "R":
                partyIconId = R.drawable.republican_icon;
                break;
            default:
                partyIconId = R.drawable.independent_icon;
        }

        setTitle(name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(partyIconId);
        fab.setPadding(0,0,0,0);

        TextView termTextView = (TextView) findViewById(R.id.senator_term);
        termTextView.setText(term);

        LinearLayout committeesLayout = (LinearLayout) findViewById(R.id.committees_layout);
        LinearLayout billsLayout = (LinearLayout) findViewById(R.id.bills_layout);

        for (String committee : committees) {
            TextView tv = new TextView(this);
            tv.setText(committee);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            tv.setPadding(0,0,0,45);
            committeesLayout.addView(tv);
        }

        for (String bill : bills) {
            TextView tv = new TextView(this);
            tv.setText(bill);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            tv.setPadding(0,0,0,45);
            billsLayout.addView(tv);
        }

        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        appBar.setBackground(new BitmapDrawable(getResources(), senator.photo));
    }
}
