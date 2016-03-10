package com.jgraycar.represent;

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
    int photoId;

    Senator(String name, String term, String party, String email,
            String website, String twitterId, String bioguideId) {
        this.name = name;
        this.term = term;
        this.photoId = R.drawable.barbara;
        this.email = email;
        this.website = website;
        this.twitterId = twitterId;
        this.bioguideId = bioguideId;
        this.party = party;
        this.committees = new String[0];
        this.bills = new String[0];
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
