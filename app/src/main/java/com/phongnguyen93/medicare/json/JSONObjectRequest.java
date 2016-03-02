package com.phongnguyen93.medicare.json;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Phong Nguyen on 11/6/2015.
 */
public class JSONObjectRequest extends AsyncTask<String, Void, JSONObject> {

    private final static String mLogTag = "Medi-Care";
    public interface AsyncResponse{
        void processFinish(JSONObject jsonObject);
    }

    public AsyncResponse delegate = null;

    public JSONObjectRequest(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObject=null;
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
            jsonObject = new JSONObject(result.toString());

        } catch (JSONException jsonEx) {
            Log.e("JSON Error", jsonEx.getMessage());
        } catch (IOException e) {
            Log.e(mLogTag, "JSON file could not be read");
        }
        return jsonObject;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    public void onPostExecute(JSONObject jsonObject) {
        delegate.processFinish(jsonObject);
    }
}
