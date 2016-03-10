package com.jgraycar.represent;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Joel on 3/7/16.
 */
class RetrieveHTTPResponseTask extends AsyncTask<URL, Void, String> {

    public AsyncResponse delegate = null;

    public RetrieveHTTPResponseTask(AsyncResponse delegate) {
        super();
        this.delegate = delegate;
    }

    protected void onPreExecute() {
        delegate.prepareStart();
    }

    protected String doInBackground(URL... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        for (URL url : urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        return stringBuilder.toString();
    }

    protected void onPostExecute(String response) {
        Log.d("T", "Response: " + response);
        delegate.processFinish(response);
    }
}
