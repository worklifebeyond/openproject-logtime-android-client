package com.digitalcreativeasia.openprojectlogtime.base;

import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BaseBottomDialog extends BottomSheetDialogFragment {

    public interface ConfirmListener{
        void onConfirm(Bundle args);
    }

    private ConfirmListener listener;

    public void setConfirmListener(ConfirmListener listener){
        this.listener = listener;
    }

    public ConfirmListener getListener(){
        return listener;
    }

}