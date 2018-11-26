package com.digitalcreativeasia.openprojectlogtime.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import com.digitalcreativeasia.openprojectlogtime.R;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

public class LightStatusBar {

    public static void inspect(Activity activity, Toolbar toolbar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.white));
        }
        toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));
        toolbar.setTitleTextColor(activity.getResources().getColor(R.color.mainSignature));
        toolbar.setSubtitleTextColor(activity.getResources().getColor(R.color.mainSignature));
        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View view = toolbar.getChildAt(0);
                if (view != null && view instanceof TextView) {
                    TextView title = (TextView) view;
                    Typeface typeface = ResourcesCompat.getFont(activity, R.font.opensans_light);
                    title.setTypeface(typeface);
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

}