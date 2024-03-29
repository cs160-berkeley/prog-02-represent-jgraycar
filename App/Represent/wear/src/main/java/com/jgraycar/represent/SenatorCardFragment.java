package com.jgraycar.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joel on 2/29/16.
 */
public class SenatorCardFragment extends CardFragment {

    public SenatorCardFragment() {
        super();
    }

    public static CardFragment create(CharSequence name, String party) {
        CardFragment cf = new SenatorCardFragment();
        Bundle bundle = new Bundle();

        bundle.putString("name", name.toString());
        bundle.putString("party", party);

        cf.setArguments(bundle);
        cf.setContentPadding(0, 0, 0, 0);
        cf.setCardMargins(0, 0, 0, 0);
        return cf;
    }

    @Override
    public View onCreateContentView (final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.senator_card_view, container, false);
        TextView senatorName = (TextView) v.findViewById(R.id.senator_name);
        TextView senatorParty = (TextView) v.findViewById(R.id.senator_party);

        Bundle args = getArguments();
        final String name = args.getString("name");
        senatorName.setText(name);

        String party = args.getString("party");
        switch(party) {
            case "D":
                senatorParty.setTextColor(getResources().getColor(R.color.democrat));
                senatorParty.setText("Democrat");
                break;
            case "R":
                senatorParty.setTextColor(getResources().getColor(R.color.republican));
                senatorParty.setText("Republican");
                break;
            default:
                senatorParty.setTextColor(getResources().getColor(R.color.independent));
                senatorParty.setText("Independent");
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(((DisplaySenatorActivity) inflater.getContext()).getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("name", name);
                sendIntent.putExtra("path", "/show_details");
                ((DisplaySenatorActivity) inflater.getContext()).getBaseContext().startService(sendIntent);
            }
        });

        return v;
    }
}
