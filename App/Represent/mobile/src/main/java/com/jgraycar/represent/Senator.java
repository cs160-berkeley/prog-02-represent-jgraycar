package com.jgraycar.represent;

import android.graphics.Bitmap;

import com.twitter.sdk.android.tweetui.TweetView;

/**
 * Created by Joel on 2/27/16.
 */
class Senator {
    String name;
    String term;
    String email;
    String website;
    String twitterId;
    String bioguideId;
    String party;
    String[] committees;
    String[] bills;
    long tweetId;
    TweetView tweetView;
    String photoUrl;
    Bitmap photo;

    Senator(String name, String term, String party, String email,
            String website, String twitterId, String bioguideId) {
        this.name = name;
        this.term = term;
        this.tweetId = 0L;
        this.email = email;
        this.website = website;
        this.twitterId = twitterId;
        this.bioguideId = bioguideId;
        this.party = party;
        this.committees = new String[0];
        this.bills = new String[0];
        this.photoUrl = "";
        this.tweetView = null;
        this.photo = null;
    }

    public int partyIconId() {
        if (party.equalsIgnoreCase("D")) {
            return R.drawable.democrat;
        } else if (party.equalsIgnoreCase("R")) {
            return R.drawable.republican;
        }
        return R.drawable.independent;
    }
}
