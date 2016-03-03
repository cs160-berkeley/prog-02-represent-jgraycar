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
        int location = getArguments().getInt("location");
        if (location == 0) {
            district.setText("Alameda County, CA");
        } else {
            district.setText("Gorham, Maine");
        }
        return v;
    }

    public static ElectionFragment create(int location) {
        ElectionFragment ef = new ElectionFragment();

        Bundle args = new Bundle();
        args.putInt("location", location);

        ef.setArguments(args);
        return ef;
    }
}
