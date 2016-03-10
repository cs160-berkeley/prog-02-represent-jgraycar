package com.jgraycar.represent;

/**
 * Created by Joel on 3/9/16.
 */
public interface AsyncResponse {
    void processFinish(String output);

    void prepareStart();
}
