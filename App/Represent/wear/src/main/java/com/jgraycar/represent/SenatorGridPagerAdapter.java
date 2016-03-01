package com.jgraycar.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;

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
        Senator senator = ((DisplaySenatorActivity) mContext).senators[col];
        CardFragment fragment = SenatorCardFragment.create(senator.name, senator.party);
        fragment.setContentPadding(0,0,0,0);
        fragment.setCardMargins(0,0,0,0);

        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        if( row == 0 ) {
            // Place image at specified position
            int photoId = ((DisplaySenatorActivity) mContext).senators[column].photoId;
            return mContext.getResources().getDrawable(photoId, null);
        } else {
            // Default to background image for row
            return GridPagerAdapter.BACKGROUND_NONE;
        }
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return 1;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return ((DisplaySenatorActivity) mContext).senators.length;
    }
}
