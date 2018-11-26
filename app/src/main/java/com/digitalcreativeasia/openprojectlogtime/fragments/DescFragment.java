package com.digitalcreativeasia.openprojectlogtime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.base.BaseBottomDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DescFragment extends BaseBottomDialog {

    public static String ARGS_CONTENT = "args.content";




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_desc, container, false);

        WebView webView = v.findViewById(R.id.webView);
        String content = (getArguments().getString(ARGS_CONTENT).equals("")) ? "No Descriptions"
                : getArguments().getString(ARGS_CONTENT);
        webView.loadData(content, "text/html", "UTF-8");


        return v;
    }

}
