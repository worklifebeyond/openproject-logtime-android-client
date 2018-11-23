package com.digitalcreativeasia.openprojectlogtime;

import android.app.ProgressDialog;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import timber.log.Timber;

import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.User;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.input_api_key)
    EditText mInputApiKey;
    @BindView(R.id.input_email)
    EditText mInputEmail;
    @BindView(R.id.button_login)
    AppCompatButton mButtonLogin;

    Snackbar mSnackbar;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Signin");

        initViews();

    }


    void showSnackBar(String message){
        mSnackbar.setText(message);
        mSnackbar.show();
    }

    void showLoading(String message){
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }


    void hideLoading(){
        mProgressDialog.dismiss();
    }

    private void initViews() {

        mProgressDialog = new ProgressDialog(this);


        mSnackbar = Snackbar.make(mButtonLogin, "", Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction("OK", view -> mSnackbar.dismiss());

        mButtonLogin.setOnClickListener(view -> {
            if (checkFormValid()) {
                String apiKey = mInputApiKey.getText().toString();
                String userIdentity = mInputEmail.getText().toString();
                //debug
                apiKey = App.getDebugKey();
                login(apiKey, userIdentity);
            }
        });

    }

    boolean checkFormValid() {
        String apikey = mInputApiKey.getText().toString();
        String userIdentity = mInputEmail.getText().toString();
        boolean apikeyValid = apikey.length() > 5;
        boolean userIdentityValid = userIdentity.length() > 8;
        if (!apikeyValid) mInputApiKey.setError("Api Key not valid");
        if (!userIdentityValid) mInputEmail.setError("User identity not valid");
        return userIdentityValid && apikeyValid;
    }


    void login(String apiKey, String userIdentity) {
        showLoading("Signin...");
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.baseUrl)
                + App.PATH.AUTH)
                .addHeaders("Authorization", Credentials.basic("apikey", apiKey))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Timber.e("resp %s", response.toString());
                        try {
                            JSONArray users = response.getJSONObject("_embedded").getJSONArray("elements");
                            checkUserValid(users, userIdentity, apiKey);
                        }catch (Exception e){
                            e.printStackTrace();
                            showSnackBar("Error on parsing data");
                        }
                    }

                    @Override
                    public void onError(ANError err) {
                        hideLoading();
                        Timber.e("err %s", err.getErrorDetail());
                        String msg = ErrorResponseInspector.inspect(err);
                        showSnackBar(msg);
                    }
                });
    }


    void checkUserValid(JSONArray users, String email, String apikey){
        boolean isUserAvailable = false;
        try{
            for(int i =0; i< users.length(); i++){
                JSONObject object = users.getJSONObject(i);
                if(object.getString("login").equals(email)){
                    isUserAvailable = true;
                    User user = new Gson().fromJson(object.toString(), User.class);
                    Timber.i("Halo %s", user.getName());
                    App.getTinyDB().putString(App.KEY.API, apikey);
                    App.addAuth();
                    this.toDashboard();
                }
            }

            if(!isUserAvailable)
                showSnackBar("Email not registered, please check again");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void toDashboard(){

    }


}
