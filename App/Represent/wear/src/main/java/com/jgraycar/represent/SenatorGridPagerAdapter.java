package com.jgraycar.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Joel on 2/29/16.
 */
public class SenatorGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;

    public SenatorGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        ArrayList<Senator> senators = ((DisplaySenatorActivity) mContext).senators;
        Fragment fragment;

        if (col < senators.size()) {
            Senator senator = senators.get(col);
            fragment = SenatorCardFragment.create(senator.name, senator.party);
            ((CardFragment) fragment).setContentPadding(0, 0, 0, 0);
            ((CardFragment) fragment).setCardMargins(0, 0, 0, 0);
        } else {
            int location = ((DisplaySenatorActivity) mContext).location;
            fragment = ElectionFragment.create(location);
        }

        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        if(row == 0 && column < ((DisplaySenatorActivity) mContext).senators.size()) {
            // Place image at specified position
            int photoId = ((DisplaySenatorActivity) mContext).senators.get(column).photoId;
            return mContext.getResources().getDrawable(photoId, null);
        } else {
            // Default to background image for row
            return GridPagerAdapter.BACKGROUND_NONE;
        }
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        ArrayList<Senator> senators = ((DisplaySenatorActivity) mContext).senators;
        if (senators.size() > 0) {
            return 1;
        }

        return 0;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        ArrayList<Senator> senators = ((DisplaySenatorActivity) mContext).senators;
        if (senators.size() > 0) {
            return ((DisplaySenatorActivity) mContext).senators.size() + 1;
        }

        return 0;
    }
}
