package com.digitalcreativeasia.openprojectlogtime.utils;

import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.pojos.StatusModel;

import java.util.ArrayList;

public class Commons {

    public static boolean isStatusStored() {

        try {
            ArrayList<Object> statusModelList
                    = App.getTinyDB().getListObject(App.KEY.LIST_STATUSES, StatusModel.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String normalizeNonZero(int num) {
        if (num < 10) return "0" + num;
        else return "" + num;
    }

}
