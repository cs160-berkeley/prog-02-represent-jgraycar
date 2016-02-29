package com.jgraycar.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListRepresentativesActivity extends AppCompatActivity {

    RecyclerView rv;
    private List<Senator> persons;

    // This method creates an ArrayList that has three Person objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData() {
        persons = new ArrayList<>();
        persons.add(new Senator("Senator Barbara Boxer", "1993 - 2016", R.drawable.barbara, R.drawable.democrat));
        persons.add(new Senator("Senator Mitch McConnell", "1993 - 2016", R.drawable.mitch, R.drawable.republican));
        persons.add(new Senator("Senator Bernie Sanders", "2007 - 2016", R.drawable.bernie, R.drawable.independent));
        // Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        // sendIntent.putExtra("CAT_NAME", "Fred");
        // startService(sendIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_representatives);
        initializeData();
        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
    }
}
