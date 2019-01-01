package com.digitalcreativeasia.openprojectlogtime;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.digitalcreativeasia.openprojectlogtime.fragments.SubmitFragment;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.user.User;
import com.digitalcreativeasia.openprojectlogtime.ui.CountupView;
import com.digitalcreativeasia.openprojectlogtime.ui.LightStatusBar;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import okhttp3.Response;
import timber.log.Timber;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OnTaskActivity extends AppCompatActivity {


    @BindView(R.id.countup)
    CountupView mCountView;
    @BindView(R.id.text_project)
    TextView mProjectText;
    @BindView(R.id.text_work_packages)
    TextView mWorkPackagesText;
    @BindView(R.id.button_submit)
    AppCompatButton mSubitButton;
    @BindView(R.id.button_cancel)
    AppCompatButton mCancelButton;

    TaskModel mTaskModel;
    private NotificationManager mNotificationManager;
    Snackbar mSnackBar;
    ProgressDialog progressDialog;
    User mUser;

    public static final String SPENT_TIME = "spent.time";
    public static final String COMMENTS = "comments";
    public static final String ACTIVITY_ID = "activity_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_task);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSnackBar = Snackbar.make(findViewById(R.id.toolbar), "", Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction("OK", view -> mSnackBar.dismiss());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Submitting...");

        mUser = App.getTinyDB().getObject(App.KEY.USER, User.class);

        mTaskModel = App.getTinyDB().getObject(App.KEY.CURRENT_TASK_MODEL, TaskModel.class);
        mProjectText.setText(mTaskModel.getLinks().getProject().getTitle());
        mWorkPackagesText.setText(mTaskModel.getSubject());

        long time = new Date().getTime() - App.getTinyDB().getLong(App.KEY.TIME_START, new Date().getTime());
        mCountView.start(time);

        mCancelButton.setOnClickListener(view -> {
            cancelTask();
            Toast.makeText(this, "Task cancelled", Toast.LENGTH_LONG).show();
            finish();
        });


        mSubitButton.setOnClickListener(view -> {
            Bundle arg = new Bundle();
            new FullScreenDialogFragment.Builder(this)
                    .setTitle("Submit Time Entry")
                    .setOnConfirmListener(result -> {
                        if (result.getBoolean("ok")) {
                            submit(
                                    result.getString(SPENT_TIME),
                                    result.getString(COMMENTS),
                                    result.getString(ACTIVITY_ID)
                            );
                        }
                    })
                    .setContent(SubmitFragment.class, arg)
                    .build()
                    .show(getSupportFragmentManager(), "dialog");
        });

    }


    void showSnackBar(String message, String customAction, CustomSnackBarListener listener) {
        mSnackBar.setText(message);
        mSnackBar.setAction(customAction, listener::onActionClicked);
        mSnackBar.show();
    }

    void submit(String spent, String comments, String activityId) {
        progressDialog.show();

        String apiKey = App.getTinyDB().getString(App.KEY.API, "");
        Map<String, String> map = new HashMap<>();
        map.put("project_id", App.getTinyDB().getString(App.KEY.CURRENT_PROJECT_ID, "0"));
        map.put("user_id", mUser.getId().toString());
        map.put("work_package_id", App.getTinyDB().getString(App.KEY.CURRENT_WORK_PACKAGE_ID, "0"));
        map.put("hours", spent);
        map.put("comments", comments);
        map.put("user_name", mUser.getFirstName()+" "+mUser.getLastName());
        map.put("user_email", mUser.getLogin());
        map.put("activity_id", activityId);
        map.put("project_name", mTaskModel.getLinks().getProject().getTitle());
        map.put("wp_name", mTaskModel.getSubject());
        Timber.e("err "+map.toString());

        AndroidNetworking.post(App.getApplication().getResources().getString(R.string.time_entries_api))
                .addHeaders("Authorization", Credentials.basic("apikey", apiKey))
                .setPriority(Priority.HIGH)
                .addBodyParameter(map)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Timber.e("eeeeeeeeeeeeeeeeeeee " + response.code());
                        progressDialog.dismiss();
                        mCountView.stop();
                        showSnackBar("Time entry submitterd", "OK", view -> {
                            cancelTask();
                            finish();
                        });
                    }

                    @Override
                    public void onError(ANError err) {
                        mCountView.pause();
                        progressDialog.dismiss();
                        err.printStackTrace();
                        Timber.e("err %s", err.getErrorDetail());
                        String msg = ErrorResponseInspector.inspect(err);
                        showSnackBar(msg, "DISMISS", view ->
                                finish());
                    }
                });
    }


    void cancelTask() {
        App.getTinyDB().putBoolean(App.KEY.IS_ON_TASK, false);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(App.KEY.NOTIFICATION_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
