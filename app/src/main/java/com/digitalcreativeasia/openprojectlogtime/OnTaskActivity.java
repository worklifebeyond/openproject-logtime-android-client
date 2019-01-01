package com.digitalcreativeasia.openprojectlogtime;

import android.app.NotificationManager;
import android.os.Bundle;

import com.digitalcreativeasia.openprojectlogtime.fragments.SubmitFragment;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.ui.CountupView;
import com.digitalcreativeasia.openprojectlogtime.ui.LightStatusBar;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_task);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTaskModel = App.getTinyDB().getObject(App.KEY.CURRENT_TASK_MODEL, TaskModel.class);
        mProjectText.setText(mTaskModel.getLinks().getProject().getTitle());
        mWorkPackagesText.setText(mTaskModel.getSubject());

        long time = new Date().getTime() - App.getTinyDB().getLong(App.KEY.TIME_START, new Date().getTime());
        mCountView.start(time);

        mCancelButton.setOnClickListener(view -> {
            App.getTinyDB().putBoolean(App.KEY.IS_ON_TASK, false);
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancel(App.KEY.NOTIFICATION_CODE);
            Toast.makeText(this, "Task cancelled", Toast.LENGTH_LONG).show();
            finish();
        });


        mSubitButton.setOnClickListener(view -> {
            Bundle arg = new Bundle();
            new FullScreenDialogFragment.Builder(this)
                    .setTitle("Submit Time Entry")
                    .setContent(SubmitFragment.class, arg)
                    .build()
                    .show(getSupportFragmentManager(), "dialog");
        });

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
