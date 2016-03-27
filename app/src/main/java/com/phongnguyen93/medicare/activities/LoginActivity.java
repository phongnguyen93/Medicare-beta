package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.functions.UserFunctions;
import com.phongnguyen93.medicare.extras.En_Decrypt;
import com.phongnguyen93.medicare.thread.network_thread.JSONObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

/**
 * Created by Phong Nguyen on 10/26/2015.
 */
public class LoginActivity extends BaseActivity implements Button.OnClickListener ,JSONObjectRequest.AsyncResponse{
    private SpotsDialog progressDialog;
    private com.rengwuxian.materialedittext.MaterialEditText edt_id, edt_pass;
    private Button btn_login;
    DbOperations dp;
    UserFunctions userFunctions;
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userFunctions = new UserFunctions(getApplicationContext());
        viewHolder();
    }

    private void viewHolder(){
        edt_id = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.edt_id);
        edt_pass = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.edt_pass);
        edt_id.setText(getResources().getString(R.string.test_account_id));
        edt_pass.setText(getResources().getString(R.string.test_account_pass));
        btn_login = (Button) findViewById(R.id.btn_login);
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
                String decrypt_id = null, decrypt_pass=null;
                    if (edt_id.getText().toString() == "" || edt_pass.getText().toString() == "") {
                        Toast.makeText(this, getResources().getString(R.string.empty_id), Toast.LENGTH_SHORT).show();
                    }else if(edt_id.getText().toString() != "" && edt_pass.getText().toString() != "")
                    {
                        try{
                            user_id = En_Decrypt.toHex(edt_id.getText().toString());
                            USER_ID = user_id;
                            user_pass = En_Decrypt.toHex(edt_pass.getText().toString());
                            Log.d("encrypt_string", user_id + "," + user_pass);
                            decrypt_id = En_Decrypt.fromHex(user_id);
                            decrypt_pass = En_Decrypt.fromHex(user_pass);
                            userFunctions.setCurrentUser(decrypt_id, decrypt_pass);
                            Log.d("decrypt_string", decrypt_id + "," + decrypt_pass);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        doLogin(user_id, user_pass);

                    }
               break;
        }
    }

    private void doLogin(String user_id, String user_pass) {
        JSONObjectRequest jsonObjectRequest =new JSONObjectRequest(this);
        String URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/login/user?id=" + user_id + "&pass=" + user_pass;
        jsonObjectRequest.execute(URL);
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

    @Override
    public void processFinish(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                progressDialog.dismiss();
                String token = validateLogin(jsonObject);
                if (token != "") {
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
