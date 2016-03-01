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
    int photoId;
    int partyId;

    Senator(String name, String term, int photoId, int partyId, String email,
            String website, String tweet) {
        this.name = name;
        this.term = term;
        this.photoId = photoId;
        this.partyId = partyId;
        this.email = email;
        this.website = website;
        this.tweet = tweet;
    }
}
