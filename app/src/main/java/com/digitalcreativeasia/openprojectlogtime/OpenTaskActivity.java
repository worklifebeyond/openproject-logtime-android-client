package com.digitalcreativeasia.openprojectlogtime;

import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.adapters.TaskListAdapter;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_task);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);

        mUser = App.getTinyDB().getObject(App.KEY.USER, User.class);
        taskModelList = new ArrayList<>();

        this.initViews();

        loadTask(String.valueOf(mUser.getId()));


    }

    void initViews() {
        mSnackBar = Snackbar.make(findViewById(R.id.toolbar), "", Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction("OK", view -> mSnackBar.dismiss());

        mRefreshLayout.setOnRefreshListener(() -> loadTask(String.valueOf(mUser.getId())));

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


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

    }
}
