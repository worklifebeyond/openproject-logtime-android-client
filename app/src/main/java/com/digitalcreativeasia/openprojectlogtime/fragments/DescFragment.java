package com.digitalcreativeasia.openprojectlogtime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.digitalcreativeasia.openprojectlogtime.R;
import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DescFragment extends Fragment implements FullScreenDialogContent {

    private FullScreenDialogController dialogController;
    public static final String ARGS_CONTENT = "args.content";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_desc, container, false);
        WebView webView = view.findViewById(R.id.webView);
        String content = getArguments().getString(ARGS_CONTENT);
        webView.loadData(content, "text/html", "UTF-8");
        return view;
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
