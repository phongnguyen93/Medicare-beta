package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.extras.En_Decrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import dmax.dialog.SpotsDialog;

/**
 * Created by Phong Nguyen on 10/26/2015.
 */
public class LoginActivity extends BaseActivity implements Button.OnClickListener {
    private SpotsDialog progressDialog;
    com.rengwuxian.materialedittext.MaterialEditText edt_id, edt_pass;
    DbOperations dp;
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_id = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.edt_id);
        edt_pass = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.edt_pass);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                progressDialog.show();
                String user_id = null, user_pass = null;
                try {
                    user_id = En_Decrypt.toHex(edt_id.getText().toString());
                    USER_ID = user_id;
                    user_pass = En_Decrypt.toHex(edt_pass.getText().toString());
                    String decrypt_id = En_Decrypt.fromHex(user_id);
                    String decrypt_pass = En_Decrypt.fromHex(user_pass);
                    Log.d("encrpyt_string", user_id + "," + user_pass);
                    Log.d("decrypt_string", decrypt_id + "," + decrypt_pass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                connection(user_id, user_pass);
                break;
        }
    }

    private void connection(String user_id, String user_pass) {
        NetworkConnection connection = new NetworkConnection();
        String URL = "http://service-phongtest.rhcloud.com/rest_web_service/login/user?id=" + user_id + "&pass=" + user_pass;
        connection.execute(URL);
    }

    private String validateLogin(JSONObject jsonObject) throws JSONException {
        boolean status = jsonObject.getBoolean("status");
        String token = "";
        if (status) {
            token = jsonObject.getString("token");
            Log.d("token", token);
            Toast.makeText(getBaseContext(), getResources().getString(R.string.login_msg_success) + token, Toast.LENGTH_SHORT).show();
            Intent t = new Intent(LoginActivity.this, MainActivity.class);
            t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(t);
        } else if (!status) {
            String error_message = jsonObject.getString("error_msg");
            Toast.makeText(getBaseContext(), getResources().getString(R.string.login_msg_fail) + "\n" + error_message, Toast.LENGTH_SHORT).show();
        }
        return token;
    }

    private class NetworkConnection extends AsyncTask<String, Void, JSONObject> {

        private final static String mLogTag = "Medi-Care";

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
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
                Log.d("JSONArray", jsonObject + ";");
            } catch (IOException e) {
                Log.e(mLogTag, "JSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "JSON file could not be converted to a JSONObject");
            }
            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            if (jsonObject != null) {
                progressDialog.dismiss();
            }
            try {

                String token = validateLogin(jsonObject);
                if(token!="") {
                    putToken(USER_ID, token);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void putToken(String user_id, String token) {
        dp = new DbOperations(this);
        dp.putToken(dp, user_id, token);
    }
}
