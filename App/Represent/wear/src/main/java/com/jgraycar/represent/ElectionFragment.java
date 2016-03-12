package com.jgraycar.represent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joel on 3/1/16.
 */
public class ElectionFragment extends Fragment {
    public ElectionFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_election, container, false);
        TextView district = (TextView) v.findViewById(R.id.districtTextView);
        TextView obamaVotes = (TextView) v.findViewById(R.id.obamaVotes);
        TextView romneyVotes = (TextView) v.findViewById(R.id.romneyVotes);
        String location = getArguments().getString("location");

        String[] parts = location.split(":");
        district.setText(parts[0]);
        obamaVotes.setText(parts[1] + "%");
        romneyVotes.setText(parts[2] + "%");
        return v;
    }

    public static ElectionFragment create(String location) {
        ElectionFragment ef = new ElectionFragment();

        Bundle args = new Bundle();
        args.putString("location", location);

        ef.setArguments(args);
        return ef;
    }
}
