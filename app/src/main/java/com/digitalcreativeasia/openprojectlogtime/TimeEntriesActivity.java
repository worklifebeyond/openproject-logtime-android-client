package com.digitalcreativeasia.openprojectlogtime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.adapters.TimeEntriesAdapter;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.TimeEntryType;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TimeEntries;
import com.digitalcreativeasia.openprojectlogtime.pojos.timeentry.TimeEntry;
import com.digitalcreativeasia.openprojectlogtime.ui.LightStatusBar;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import timber.log.Timber;


public class TimeEntriesActivity extends AppCompatActivity implements TimeEntriesAdapter.SelectListener {

    public static final String INTENT_TASK_MODEL = "intent.task.model";

    ArrayList<Object> timeEntryTypes;
    List<TimeEntry> timeEntriesList;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.text_project)
    TextView mProjectText;
    @BindView(R.id.text_work_packages)
    TextView mWorkPackagesText;

    @BindView(R.id.add_icon)
    ImageView mAddImage;
    @BindView(R.id.text_add)
    TextView mAddText;

    TaskModel mTaskModel;
    Snackbar mSnackBar;
    TimeEntriesAdapter mAdapter;

    private static final String NOTIFICATION_CHANNEL_ID = "com.digitalcreativeasia.openprojectlogtime.taskrunning";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "ONTASK RUNNING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_entries);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);

        mTaskModel = new Gson().fromJson(getIntent().getStringExtra(INTENT_TASK_MODEL), TaskModel.class);


        mAddImage.setOnClickListener(view -> addTimeEntry());
        mAddText.setOnClickListener(view -> addTimeEntry());

        timeEntryTypes = new ArrayList<>();
        timeEntriesList = new ArrayList<>();

        getTimeEntryTypes();
        initViews();
    }

    private void addTimeEntry(){
        if (App.getTinyDB().getBoolean(App.KEY.IS_ON_TASK)) {
            showSnackBar("You have a task that has not been completed.", "GOTO TASK",
                    view1 -> {
                        startActivity(new Intent(this, OnTaskActivity.class));
                    });
        } else {
            String projectId = mTaskModel.getLinks().getProject().getHref();
            String workPackagesId = mTaskModel.getId().toString();
            projectId = projectId.substring(projectId.lastIndexOf("/") + 1).trim();
            String wpName = mTaskModel.getSubject();

            App.getTinyDB().putBoolean(App.KEY.IS_ON_TASK, true);
            App.getTinyDB().putString(App.KEY.CURRENT_PROJECT_ID, projectId);
            App.getTinyDB().putString(App.KEY.CURRENT_WORK_PACKAGE_ID, workPackagesId);
            App.getTinyDB().putString(App.KEY.CURRENT_WORK_PACKAGE_NAME, wpName);
            App.getTinyDB().putLong(App.KEY.TIME_START, new Date().getTime());
            App.getTinyDB().putObject(App.KEY.CURRENT_TASK_MODEL, mTaskModel);

            showNotification(wpName);
            finish();

        }
    }

    private void showNotification(String desc) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, OnTaskActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm_icon)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentTitle("LogTimer")
                .setContentInfo(desc)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(desc);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(true);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
        notificationManager.notify(App.KEY.NOTIFICATION_CODE, builder.build());
    }

    void showSnackBar(String message, String customAction, CustomSnackBarListener listener) {
        mSnackBar.setText(message);
        mSnackBar.setAction(customAction, listener::onActionClicked);
        mSnackBar.show();
    }


    private void initViews() {
        mRefreshLayout.setOnRefreshListener(() ->
                getTimeEntries(false, String.valueOf(mTaskModel.getId())));
        mSnackBar = Snackbar.make(findViewById(R.id.toolbar), "", Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction("OK", view -> mSnackBar.dismiss());

        mProjectText.setText(mTaskModel.getLinks().getProject().getTitle());
        mWorkPackagesText.setText(mTaskModel.getSubject());

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }


    void getTimeEntryTypes() {
        mRefreshLayout.setRefreshing(true);
        String apiKey = App.getTinyDB().getString(App.KEY.API, "");
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.time_entries_api)
                + App.PATH.ENUM_LIST)
                .addHeaders("Authorization", Credentials.basic("apikey", apiKey))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                TimeEntryType entryType = new Gson().fromJson(response.getJSONObject(i).toString(), TimeEntryType.class);
                                timeEntryTypes.add(entryType);
                            }
                            App.getTinyDB().putListObject(App.KEY.ENTRY_TYPE, timeEntryTypes);
                            getTimeEntries(true, String.valueOf(mTaskModel.getId()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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


    void getTimeEntries(boolean initRefresh, String workPackageId) {
        if (!initRefresh)
            mRefreshLayout.setRefreshing(true);
        String url = String.format(App.PATH.TIME_ENTRIES_LIST, workPackageId);
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.baseUrl)
                + url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mRefreshLayout.setRefreshing(false);
                        Timber.i("resp: %s", response.toString());
                        try {
                            JSONArray array = response.getJSONObject("_embedded").getJSONArray("elements");
                            timeEntriesList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                TimeEntry model = new Gson().fromJson(array.getJSONObject(i).toString(), TimeEntry.class);
                                timeEntriesList.add(model);
                            }
                            updateList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void updateList() {
        if (mAdapter == null) {
            mAdapter = new TimeEntriesAdapter(this, timeEntriesList, this);
            mRecyclerView.setAdapter(mAdapter);
        } else mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(TimeEntry model) {

    }
}
