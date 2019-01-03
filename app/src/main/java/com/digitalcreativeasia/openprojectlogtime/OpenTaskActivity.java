package com.digitalcreativeasia.openprojectlogtime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.adapters.TaskListAdapter;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.StatusModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.user.User;
import com.digitalcreativeasia.openprojectlogtime.ui.LightStatusBar;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class OpenTaskActivity extends AppCompatActivity implements TaskListAdapter.SelectListener {

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    Snackbar mSnackBar;
    User mUser;

    List<TaskModel> taskModelList;
    TaskListAdapter mAdapter;

    private static final String NOTIFICATION_CHANNEL_ID = "com.digitalcreativeasia.openprojectlogtime.taskrunning";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "ONTASK RUNNING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_task);
        getListStatuses();
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);

        mUser = App.getTinyDB().getObject(App.KEY.USER, User.class);
        taskModelList = new ArrayList<>();

        if (App.getTinyDB().getBoolean(App.KEY.IS_ON_TASK)) {
            startActivity(new Intent(this, OnTaskActivity.class));
        }
        this.initViews();

        //loadTask(String.valueOf(mUser.getId()));


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTask(String.valueOf(mUser.getId()));
    }

    void initViews() {
        mSnackBar = Snackbar.make(findViewById(R.id.toolbar), "", Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction("OK", view -> mSnackBar.dismiss());

        mRefreshLayout.setOnRefreshListener(() -> loadTask(String.valueOf(mUser.getId())));

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (App.getTinyDB().getBoolean(App.KEY.IS_ON_TASK)) {
            showNotification(App.getTinyDB().getString(App.KEY.CURRENT_WORK_PACKAGE_NAME, "Task"));
            startActivity(new Intent(this, OnTaskActivity.class));
        }

    }

    private void showNotification(String desc) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, OnTaskActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm_icon)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentTitle(desc)
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
                        try {
                            JSONArray array = response.getJSONObject("_embedded").getJSONArray("elements");
                            taskModelList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                TaskModel model = new Gson().fromJson(array.getJSONObject(i).toString(), TaskModel.class);
                                taskModelList.add(model);
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


    void getListStatuses() {
        AndroidNetworking.get(App.getApplication().getResources().getString(R.string.baseUrl)
                + App.PATH.GETLIST_STATUS)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Timber.i("resp: %s", response.toString());
                        try {
                            JSONArray array = response.getJSONObject("_embedded").getJSONArray("elements");
                            ArrayList<Object> listStatus = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                StatusModel model = new Gson().fromJson(array.getJSONObject(i).toString(),
                                        StatusModel.class);
                                listStatus.add(model);
                            }
                            App.getTinyDB().putListObject(App.KEY.LIST_STATUSES, listStatus);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError err) {
                        Timber.e("err %s", err.getErrorDetail());
                        String msg = ErrorResponseInspector.inspect(err);
                        showSnackBar(msg, "Exit", view -> finish());
                    }
                });

    }


    void updateList() {
        if (mAdapter == null) {
            mAdapter = new TaskListAdapter(this, taskModelList, this);
            mRecyclerView.setAdapter(mAdapter);
        } else mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        if (!mRefreshLayout.isRefreshing())
            super.onBackPressed();
    }

    @Override
    public void onSelect(TaskModel model) {
        Intent intent = new Intent(this, TimeEntriesActivity.class);
        intent.putExtra(TimeEntriesActivity.INTENT_TASK_MODEL,
                new Gson().toJson(model));
        startActivity(intent);

    }

    @Override
    public void onRefresh(boolean success) {
        if (success) {
            Toast.makeText(this, "Sukses update presentasi", Toast.LENGTH_LONG).show();
            loadTask(String.valueOf(mUser.getId()));
        } else {
            showSnackBar("Gagal update prosentase", "RELOAD", view -> {
                loadTask(String.valueOf(mUser.getId()));
            });
        }
    }
}
