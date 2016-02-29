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
        System.out.println("poop");
        Intent intent = new Intent(this, ListRepresentativesActivity.class);
        startActivity(intent);
    }
}
