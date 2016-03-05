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

        final Bundle args = getArguments();
        senatorName.setText(args.getString("name"));

        String party = args.getString("party");
        senatorParty.setText(party);
        switch(party) {
            case "Democrat":
                senatorParty.setTextColor(getResources().getColor(R.color.democrat));
                break;
            case "Republican":
                senatorParty.setTextColor(getResources().getColor(R.color.republican));
                break;
            default:
                senatorParty.setTextColor(getResources().getColor(R.color.independent));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(((DisplaySenatorActivity) inflater.getContext()).getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtras(args);
                ((DisplaySenatorActivity) inflater.getContext()).getBaseContext().startService(sendIntent);
            }
        });

        return v;
    }
}
