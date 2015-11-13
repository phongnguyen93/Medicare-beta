package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.extras.En_Decrypt;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import dmax.dialog.SpotsDialog;

/**
 * Created by Phong Nguyen on 10/23/2015.
 */
public class SignupActivity extends BaseActivity implements Button.OnClickListener {
    private String USER_ID;
    SpotsDialog progressDialog;
    com.rengwuxian.materialedittext.MaterialEditText edit_id,edit_name,edit_pass,edit_email,edit_phone;
    Button btn_create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        viewHolder();



    }


    public void viewHolder (){
        edit_id= (MaterialEditText) findViewById(R.id.edit_id);
        edit_name= (MaterialEditText) findViewById(R.id.edit_name);
        edit_pass= (MaterialEditText) findViewById(R.id.edit_pass);
        edit_email=(MaterialEditText)findViewById(R.id.edit_email);
        edit_phone=(MaterialEditText)findViewById(R.id.edit_phone);
        btn_create=(Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
            signUp();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void signUp() throws UnsupportedEncodingException {
        String id = edit_id.getText().toString().trim();
        String name = edit_name.getText().toString();
        String pass = edit_pass.getText().toString().trim();
        String email = edit_email.getText().toString().trim();
        String phone = edit_phone.getText().toString().trim();
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if(!validateInput(id,0)) {
            validationError =true;
            edit_id.requestFocus();
            Toast.makeText(this, validationErrorMessage.append(getString(R.string.invalid_id)), Toast.LENGTH_SHORT).show();
            Log.e("Input Error", "Invalid ID");
        }
        if(!validateInput(pass,1)) {
            validationError =true;
            edit_pass.requestFocus();
            Toast.makeText(this, validationErrorMessage.append(getString(R.string.invalid_pass)), Toast.LENGTH_SHORT).show();
            Log.e("Input Error", "Invalid Password");
        }
        if(!validateInput(email,2)) {
            validationError=true;
            edit_email.requestFocus();
            Toast.makeText(this, validationErrorMessage.append(getString(R.string.invalid_email)), Toast.LENGTH_SHORT).show();
            Log.e("Input Error", "Invalid Email");
        }
        if(name.length()==0 ){
            validationError=true;
            edit_name.requestFocus();
            Toast.makeText(this, validationErrorMessage.append(getString(R.string.invalid_name)), Toast.LENGTH_SHORT).show();
            Log.e("Input Error", "Invalid Email");
        }
        if(!validationError) {
            String encrypt_name =  URLEncoder.encode(name, "UTF-8");
            progressDialog = new SpotsDialog(this, R.style.Custom);
            progressDialog.show();
            String encode = En_Decrypt.toHex(id);
            USER_ID=encode;
            connection(id,pass,email,encrypt_name,phone);

        }
    }

    private void connection(String id, String pass, String email, String name, String phone) {
        NetworkConnection connection =new NetworkConnection();
        String URL ="http://service-phongtest.rhcloud.com/rest_web_service/register/user?id="+id+
                "&password="+pass+
                "&name="+name+
                "&email="+email+
                "&phone="+phone+"";
        connection.execute(URL);
    }

    public boolean validateInput(String input,int type){
        if(input.length()==0)
            return false;
        for(int i=0;i<input.length();i++) {
            switch (type){
                case 0: //validate id input( letter or digit) , max =15 chars
                    final String namePattern = "((?=.*[a-zA-Z0-9]).{6,20})";
                    if(!input.matches(namePattern)) {return false;}
                    break;
                case 1: //validate password ( must have letter and digit), min = 6 chars
                    final String passPattern = "((?=.*\\d)(?=.*[a-z]).{6,20})";
                    if(!input.matches(passPattern)){return false;}
                    break;
                case 2: //validate email ( mail domain: abc@xyz.com)
                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if(!input.matches(emailPattern)){return false;}
                    break;
            }

        }
        return true;
    }
    public class NetworkConnection extends AsyncTask<String, Void, JSONObject> {

        private final static String mLogTag = "Medi-Care";

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
                jsonObject =  new JSONObject(result.toString());
            } catch (IOException e) {
                Log.e(mLogTag, "JSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "JSON file could not be converted to a JSONArray");
            }
            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {


                validateSignUp(jsonObject);

        }
    }

    private void validateSignUp(JSONObject jsonObject) {
        try {
            boolean status = jsonObject.getBoolean("status");
            if(status) {
                String token = jsonObject.getString("token");
                DbOperations dp = new DbOperations(this);
                Log.d("medicare",USER_ID);
                dp.putToken(dp,USER_ID,token);
                Toast.makeText(this, getResources().getString(R.string.signup_success), Toast.LENGTH_SHORT).show();
                Intent t = new Intent(SignupActivity.this, MainActivity.class);
                t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(t);
            } else  {
                String error_message = jsonObject.getString("error_msg");
                Toast.makeText(getBaseContext(), getResources().getString(R.string.signup_msg_fail) + "\n" + error_message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
