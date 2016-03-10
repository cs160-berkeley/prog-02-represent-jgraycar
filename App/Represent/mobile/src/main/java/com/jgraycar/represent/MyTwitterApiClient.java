package com.jgraycar.represent;

/**
 * Created by Joel on 3/10/16.
 */
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit.http.GET;
import retrofit.http.Query;

class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(AppSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public UserDataService getUserDataService() {
        return getService(UserDataService.class);
    }
}

// example users/show service endpoint
interface UserDataService {
    @GET("/1.1/users/show.json")
    void show(@Query("screen_name") String screenName, Callback<User> cb);
}
