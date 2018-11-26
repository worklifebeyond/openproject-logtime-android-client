package com.digitalcreativeasia.openprojectlogtime;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.digitalcreativeasia.openprojectlogtime.storage.TinyDB;
import com.digitalcreativeasia.openprojectlogtime.logger.CrashReportingTree;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class App extends Application {

    private static Application sApplication;
    private static TinyDB tinyDB;

    public static Application getApplication() {
        return sApplication;
    }

    public static TinyDB getTinyDB() {
        return tinyDB;
    }

    public interface PATH {
        String AUTH = "project/api/v3/users?pageSize=500";
        String OPEN_TASK ="project/api/v3/work_packages?filters=[{\"assignee\":{\"operator\":\"=\",\"values\":[\"%s\"]}},{\"status\":{\"operator\":\"o\",\"values\":[\"5\",\"3\"]}}]&offset=1&pageSize=500&sortBy=[[\"updated_at\",\"desc\"]]";
    }

    public interface KEY {
        String API = "user.api.key";
        String USER = "uer.key";
        String IS_LOGGED_IN = "is.logged.in";
    }


    // debug api key
    public static String getDebugKey(){
        return getApplication().getString(R.string.debug_api_key);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        tinyDB = new TinyDB(sApplication);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

    }


    public static void plantAuth(){
        String apiKey = getTinyDB().getString(KEY.API, "");
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .authenticator((route, response) -> response.request().newBuilder()
                        .header("Authorization", Credentials.basic("apikey", apiKey))
                        .build())
                .build();
        AndroidNetworking.initialize(getApplication().getApplicationContext(), okHttpClient);
    }


}
