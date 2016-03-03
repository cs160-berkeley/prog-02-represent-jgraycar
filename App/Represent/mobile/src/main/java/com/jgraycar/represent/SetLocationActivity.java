package com.jgraycar.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;

public class SetLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
    }

    public void useZipCode(View view) {
        Intent intent = new Intent(this, ListRepresentativesActivity.class);
        intent.putExtra(ListRepresentativesActivity.LOCATION_KEY, 1);
        startActivity(intent);
    }

    public void useCurrentLocation(View view) {
        Intent intent = new Intent(this, ListRepresentativesActivity.class);
        intent.putExtra(ListRepresentativesActivity.LOCATION_KEY, 0);
        startActivity(intent);
    }
}
