package com.digitalcreativeasia.openprojectlogtime;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.digitalcreativeasia.openprojectlogtime.storage.TinyDB;
import com.digitalcreativeasia.openprojectlogtime.logger.CrashReportingTree;

import net.danlew.android.joda.JodaTimeAndroid;

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
        String AUTH = "api/v3/users/me";
        String OPEN_TASK = "api/v3/work_packages?filters=[{\"assignee\":{\"operator\":\"=\",\"values\":[\"%s\"]}},{\"status\":{\"operator\":\"o\",\"values\":[\"5\",\"3\"]}}]&offset=1&pageSize=500&sortBy=[[\"updated_at\",\"desc\"]]";
        String TIME_ENTRIES_LIST = "api/v3/time_entries?filters=[{ \"work_package\": { \"operator\": \"=\", \"values\": [\"%s\"] } }]&pageSize=500&sortBy=[[\"id\",\"desc\"]]";
        String ENUM_LIST = "enum";
        String UPDATE_WORK_PACKAGES = "api/v3/work_packages/";
        String GETLIST_STATUS = "api/v3/statuses";
        String LIST_ACTIVITY = "api/v3/work_packages/%s/activities";
    }

    public interface KEY {
        String API = "user.api.key";
        String USER = "uer.key";
        String IS_LOGGED_IN = "is.logged.in";
        String IS_ON_TASK = "is.on.task";
        String TIME_START = "time.start";
        String CURRENT_PROJECT_ID = "current.project.id";
        String CURRENT_WORK_PACKAGE_ID = "current.work.package.id";
        String CURRENT_WORK_PACKAGE_NAME = "current.work.packages.name";
        String CURRENT_TASK_MODEL = "current.task.model";
        int NOTIFICATION_CODE = 1991;
        String ENTRY_TYPE = "current.entry.type";
        String LIST_STATUSES = "list.of.status";
    }


    // debug api key
    public static String getDebugKey() {
        return getApplication().getString(R.string.debug_api_key);
    }


    public static String getAuthHeader(){
        String apiKey = tinyDB.getString(KEY.API, "");
        return Credentials.basic("apikey", apiKey);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        sApplication = this;
        tinyDB = new TinyDB(sApplication);

        if (tinyDB.getBoolean(KEY.IS_LOGGED_IN)) {
            String apiKey = tinyDB.getString(KEY.API, "");
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .authenticator((route, response) -> response.request().newBuilder()
                            .header("Authorization", Credentials.basic("apikey", apiKey))
                            .build())
                    .build();
            AndroidNetworking.initialize(getApplication().getApplicationContext(), okHttpClient);
        } else {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .build();
            AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

    }


    public static void plantAuth() {
        String apiKey = getTinyDB().getString(KEY.API, "");
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .authenticator((route, response) -> response.request().newBuilder()
                        .header("Authorization", Credentials.basic("apikey", apiKey))
                        .build())
                .build();
        AndroidNetworking.initialize(getApplication().getApplicationContext(), okHttpClient);
    }


}
