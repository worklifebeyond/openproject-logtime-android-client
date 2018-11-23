package com.digitalcreativeasia.openprojectlogtime;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import timber.log.Timber;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.input_api_key)
    EditText mInputApiKey;
    @BindView(R.id.input_user_number)
    EditText mInputUserIdentity;
    @BindView(R.id.button_login)
    AppCompatButton mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

    }

    private void initViews() {
        mButtonLogin.setOnClickListener(view -> {
            if (checkFormValid()) {
                String apiKey = mInputApiKey.getText().toString();
                String userIdentity = mInputUserIdentity.getText().toString();
                login(apiKey, userIdentity);
            }
        });

    }

    boolean checkFormValid() {
        String apikey = mInputApiKey.getText().toString();
        String userIdentity = mInputUserIdentity.getText().toString();
        boolean apikeyValid = apikey.length() > 5;
        boolean userIdentityValid = userIdentity.length() > 0;
        if (!apikeyValid) mInputApiKey.setError("Api Key not valid");
        if (!userIdentityValid) mInputUserIdentity.setError("User identity not valid");
        return userIdentityValid && apikeyValid;
    }


    void login(String apiKey, String userIdentity) {
        // debug
        apiKey = App.getDebugKey();
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.baseUrl)
                + App.PATH.AUTH + userIdentity)
                .addHeaders("Authorization", Credentials.basic("apikey", apiKey))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Timber.e("resp %s", response.toString());
                    }

                    @Override
                    public void onError(ANError err) {
                        Timber.e("err %s", err.getErrorDetail());
                    }
                });
    }


}
