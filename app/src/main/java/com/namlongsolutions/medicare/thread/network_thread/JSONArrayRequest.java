package com.namlongsolutions.medicare.thread.network_thread;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Phong Nguyen on 11/6/2015.
 */
public class JSONArrayRequest extends AsyncTask<String, Void, JSONArray> {

    private final static String mLogTag = "Medi-Care";
    public interface AsyncResponse{
        void processFinish(JSONArray jsonArray);
    }

    public AsyncResponse delegate = null;

    public JSONArrayRequest(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray jsonArray=null;
        try {
            // Open a stream from the URL
            InputStream stream = new URL(params[0]).openStream();
            String line;
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                // Read and save each line of the stream
                result.append(line);
            }
            // Close the stream
            reader.close();
            stream.close();

            // Convert result to JSONObject
            jsonArray = new JSONArray(result.toString());
            Log.d(mLogTag,result.toString());
        } catch (JSONException jsonEx) {
            Log.e("JSON Error", jsonEx.getMessage());
            if (jsonArray.length() == 0) {
                Log.e("Doctor List", "is empty");
            }
        } catch (IOException e) {
            Log.e(mLogTag, "JSON file could not be read");
        }
        return jsonArray;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    public void onPostExecute(JSONArray jsonArray) {
        delegate.processFinish(jsonArray);
    }
}
