package com.digitalcreativeasia.openprojectlogtime.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.OnTaskActivity;
import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.interfaces.CustomSnackBarListener;
import com.digitalcreativeasia.openprojectlogtime.pojos.TimeEntryType;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.user.User;
import com.digitalcreativeasia.openprojectlogtime.utils.ErrorResponseInspector;
import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.google.android.material.snackbar.Snackbar;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import okhttp3.Response;
import timber.log.Timber;

public class SubmitFragment extends Fragment implements FullScreenDialogContent {

    private FullScreenDialogController dialogController;

    @BindView(R.id.text_project)
    TextView mProjectText;
    @BindView(R.id.text_work_packages)
    TextView mWpText;
    @BindView(R.id.text_timespent)
    TextView mTimespentText;
    @BindView(R.id.spinner_type)
    NiceSpinner mSpinnerType;
    @BindView(R.id.edit_comment)
    EditText mCommentEdit;
    @BindView(R.id.button_submit)
    AppCompatButton mSubmitButton;

    TaskModel mTaskModel;


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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgament_submit_entry, container, false);
        ButterKnife.bind(this, view);

        mTaskModel = App.getTinyDB().getObject(App.KEY.CURRENT_TASK_MODEL, TaskModel.class);
        mProjectText.setText(mTaskModel.getLinks().getProject().getTitle());
        mWpText.setText(mTaskModel.getSubject());

        ArrayList<Object> entryTypes = App.getTinyDB().getListObject(App.KEY.ENTRY_TYPE, TimeEntryType.class);
        List<String> titles = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < entryTypes.size(); i++) {
            TimeEntryType entryType = (TimeEntryType) entryTypes.get(i);
            if (entryType.getActive() == 1) {
                titles.add(entryType.getName());
                ids.add(entryType.getId().toString());
            }
        }

        mSpinnerType.attachDataSource(titles);

        double spent =
                (new Date().getTime() - App.getTinyDB().getLong(App.KEY.TIME_START, new Date().getTime())) / 3.6e+6;
        mTimespentText.setText(String.valueOf(round(spent)) + "Hrs");

        mSubmitButton.setOnClickListener(view1 -> {
            if(mCommentEdit.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Comment is empty", Toast.LENGTH_LONG).show();
            }else {
                Bundle args = new Bundle();
                args.putBoolean("ok", true);
                args.putString(OnTaskActivity.SPENT_TIME, String.valueOf(spent));
                args.putString(OnTaskActivity.COMMENTS, mCommentEdit.getText().toString());
                args.putString(OnTaskActivity.ACTIVITY_ID, ids.get(mSpinnerType.getSelectedIndex()));
                dialogController.confirm(args);
            }
        });

        return view;
    }

    Double round(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


}
