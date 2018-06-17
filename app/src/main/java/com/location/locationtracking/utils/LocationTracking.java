package com.location.locationtracking.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;

public class LocationTracking extends Application {

    private static LocationTracking locationTracking = null;
    private OkHttpClient mOkhttpClient = null;
    private Handler mHandler = null;

    public static synchronized LocationTracking getInstance() {
        if (locationTracking == null) {
            return locationTracking = new LocationTracking();
        }
        return locationTracking;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationTracking = this;
    }

    public OkHttpClient getOkhttpClient() {
        if (mOkhttpClient == null) {
            mOkhttpClient = new OkHttpClient.Builder().build();
        }
        return mOkhttpClient;
    }

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public String getCurrentTime() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(new Date());
    }
}
