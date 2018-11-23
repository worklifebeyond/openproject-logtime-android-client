package com.digitalcreativeasia.openprojectlogtime;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.digitalcreativeasia.openprojectlogtime.storage.TinyDB;

import okhttp3.OkHttpClient;

public class App extends Application {

    private static Application sApplication;
    private static TinyDB tinyDB;

    public static Application getApplication() {
        return sApplication;
    }

    public static TinyDB getTinyDB() {
        return tinyDB;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        tinyDB = new TinyDB(sApplication);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

    }

}
