package com.digitalcreativeasia.openprojectlogtime;

import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.user.User;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class OpenTaskActivity extends AppCompatActivity {

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    Snackbar mSnackBar;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_task);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = App.getTinyDB().getObject(App.KEY.USER, User.class);

        this.initViews();

        loadTask(String.valueOf(mUser.getId()));


    }

    void initViews() {
        mSnackBar = Snackbar.make(findViewById(R.id.toolbar), "", Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction("OK", view -> mSnackBar.dismiss());

        mRefreshLayout.setOnRefreshListener(() -> loadTask(String.valueOf(mUser.getId())));
    }

    void showSnackBar(String message, String customAction, CustomSnackBarListener listener) {
        mSnackBar.setText(message);
        mSnackBar.setAction(customAction, listener::onActionClicked);
        mSnackBar.show();
    }

    void showSnackBar(String message) {
        mSnackBar.setText(message);
        mSnackBar.show();
    }

    void loadTask(String userID) {
        mRefreshLayout.setRefreshing(true);
        String url = String.format(App.PATH.OPEN_TASK, userID);
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.baseUrl)
                + url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mRefreshLayout.setRefreshing(false);
                        Timber.i("resp: %s", response.toString());
                    }

                    @Override
                    public void onError(ANError err) {
                        mRefreshLayout.setRefreshing(false);
                        Timber.e("err %s", err.getErrorDetail());
                        String msg = ErrorResponseInspector.inspect(err);
                        showSnackBar(msg, "Exit", view -> finish());
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if (!mRefreshLayout.isRefreshing())
            super.onBackPressed();
    }
}
