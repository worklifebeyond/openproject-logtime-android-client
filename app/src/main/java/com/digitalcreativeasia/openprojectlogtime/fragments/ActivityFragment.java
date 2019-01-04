package com.digitalcreativeasia.openprojectlogtime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.adapters.ActivityAdapter;
import com.digitalcreativeasia.openprojectlogtime.pojos.activity.ActivityModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.activity.RowComment;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ActivityFragment extends Fragment implements FullScreenDialogContent {

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static final String WORK_PACKAGES_ID = "work.packages.id";

    FullScreenDialogController dialogController;
    String mWPId;
    ActivityAdapter mAdapter;
    List<RowComment> comments;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comments = new ArrayList<>();
        mWPId = getArguments().getString(WORK_PACKAGES_ID);

        mRefreshLayout.setOnRefreshListener(this::loadActivity);
        loadActivity();
    }


    void loadActivity() {
        mRefreshLayout.setRefreshing(true);
        String url = String.format(
                (App.getApplication().getString(R.string.baseUrl) + App.PATH.LIST_ACTIVITY),
                mWPId
        );
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Timber.e(response.toString());
                        mRefreshLayout.setRefreshing(false);
                        try {
                            List<ActivityModel> models = new ArrayList<>();
                            JSONArray array = response.getJSONObject("_embedded").getJSONArray("elements");
                            for (int i = 0; i < array.length(); i++) {
                                ActivityModel m = new Gson().fromJson(array.getJSONObject(i).toString(), ActivityModel.class);
                                models.add(m);
                            }

                            comments.clear();
                            for (int i = 0; i < models.size(); i++) {
                                ActivityModel model = models.get(i);
                                if (model.getComment().getRaw().equals("")) {
                                    for (int j = 0; j < model.getDetails().size(); j++) {
                                        RowComment rc = new RowComment();
                                        if (j == 0) {
                                            rc.setDate(model.getCreatedAt());
                                            rc.setContent(model.getDetails().get(j).getHtml());
                                            comments.add(rc);
                                        } else {
                                            rc.setDate("");
                                            rc.setContent(model.getDetails().get(j).getHtml());
                                            comments.add(rc);
                                        }
                                    }
                                } else {
                                    RowComment r2 = new RowComment();
                                    r2.setDate(model.getCreatedAt());
                                    r2.setContent("<b>Comments: </b>" + model.getComment().getHtml());
                                    comments.add(r2);
                                    for (int j = 0; j < model.getDetails().size(); j++) {
                                        RowComment rc = new RowComment();
                                        rc.setDate("");
                                        rc.setContent(model.getDetails().get(j).getHtml());
                                        comments.add(rc);
                                    }
                                }
                            }
                            setData();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError err) {
                        mRefreshLayout.setRefreshing(false);
                        Timber.e("err %s", err.getErrorDetail());
                        String msg = ErrorResponseInspector.inspect(err);
                        Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", view -> dialogController.discard());
                    }
                });
    }

    void setData() {
        if (mAdapter == null) {
            mAdapter = new ActivityAdapter(getContext(), comments, model -> {

            });
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, true));
            mRecyclerView.setAdapter(mAdapter);

            Timber.e("adapter");
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDialogCreated(FullScreenDialogController dialogController) {
        this.dialogController = dialogController;
    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialogController) {
        return false;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialogController) {
        return false;
    }
}
