package com.digitalcreativeasia.openprojectlogtime.utils;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

import timber.log.Timber;

public class ErrorResponseInspector {

    private static String parseErrorBody(String body) {
        try {
            JSONObject object = new JSONObject(body);
            return object.getString("message");
        } catch (Exception e) {
            e.printStackTrace();
            return "Kesalahan jaringan, cek kembali koneksi internet Anda";
        }
    }

    public static String inspect(ANError err) {
        Timber.e(err.getErrorBody());
        return parseErrorBody(err.getErrorBody());
    }

}
