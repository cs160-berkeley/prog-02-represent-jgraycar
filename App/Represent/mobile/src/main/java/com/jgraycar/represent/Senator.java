package com.jgraycar.represent;

/**
 * Created by Joel on 2/27/16.
 */
class Senator {
    String name;
    String term;
    String email;
    String website;
    String tweet;
    String party;
    int photoId;

    Senator(String name, String term, int photoId, String party, String email,
            String website, String tweet) {
        this.name = name;
        this.term = term;
        this.photoId = photoId;
        this.email = email;
        this.website = website;
        this.tweet = tweet;
        this.party = party;
    }

    public int partyIconId() {
        if (party.equalsIgnoreCase("democrat")) {
            return R.drawable.democrat;
        } else if (party.equalsIgnoreCase("republican")) {
            return R.drawable.republican;
        }
        return R.drawable.independent;
    }
}
