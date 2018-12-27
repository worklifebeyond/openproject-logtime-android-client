package com.digitalcreativeasia.openprojectlogtime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import org.angmarch.views.NiceSpinner;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

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



        return view;
    }

}
