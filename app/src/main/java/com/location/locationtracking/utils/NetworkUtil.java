package com.location.locationtracking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.location.locationtracking.interfaces.NetworkCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NetworkUtil {

    private final String TAG = "NetworkUtil";

    private static final String UPDATE_LOCATION = "https://nitsbirco7.execute-api.ap-south-1.amazonaws.com/latest/";

    private Context superContext;
    private MediaType JSON;

    public NetworkUtil(Context context) {
        this.superContext = context;
        JSON = MediaType.parse("application/json; charset=utf-8");
    }

    public void updateLocation(double latitude, double longitude, final NetworkCallBack networkCallBack) {

        JSONObject jsonRequest = new JSONObject();
        JSONArray coordinateArray = new JSONArray();
        try {
            // network requests if necessary
            coordinateArray.put(latitude);
            coordinateArray.put(longitude);
            jsonRequest.put("name", "test");
            jsonRequest.put("loc", coordinateArray);
            jsonRequest.put("time", LocationTracking.getInstance().getCurrentTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            RequestBody requestBody = RequestBody.create(JSON, jsonRequest.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(UPDATE_LOCATION)
                    .post(requestBody)
                    .build();
            Log.d(TAG, jsonRequest.toString());
            LocationTracking.getInstance().getOkhttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException ioException) {
                    LocationTracking.getInstance().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            networkCallBack.onError(ioException);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String stringResponse = response.body().string();
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LocationTracking.getInstance().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            networkCallBack.onSuccess(-1, stringResponse);

                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
