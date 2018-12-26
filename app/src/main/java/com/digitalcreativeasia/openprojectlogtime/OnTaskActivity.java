package com.digitalcreativeasia.openprojectlogtime;

import android.os.Bundle;

import com.digitalcreativeasia.openprojectlogtime.ui.LightStatusBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.ButterKnife;

import android.view.View;

public class OnTaskActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_task);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LightStatusBar.inspect(this, toolbar);
        setSupportActionBar(toolbar);

    }

}
