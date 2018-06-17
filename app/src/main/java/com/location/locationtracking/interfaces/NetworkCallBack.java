package com.location.locationtracking.interfaces;

public interface NetworkCallBack {

    void onSuccess(int type, Object... object);

    void onError(Exception exception);
}
