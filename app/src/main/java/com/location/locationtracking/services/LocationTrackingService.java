package com.location.locationtracking.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.location.locationtracking.interfaces.NetworkCallBack;
import com.location.locationtracking.utils.NetworkUtil;

import java.util.Calendar;

public class LocationTrackingService extends Service implements LocationListener {

    private static final String TAG = "LocationTrackingService";
    LocationManager mLocationManager;

    @Override
    public void onCreate() {
        this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY;
        }
        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000 * 60 * 60, 5, this);
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent myIntent = new Intent(getApplicationContext(), LocationTrackingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent myIntent = new Intent(getApplicationContext(), LocationTrackingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(), "Start Alarm", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return ;
        Toast.makeText(getApplicationContext(), Double.toString(location.getLatitude()) + " " +
                Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
        handleLocationUpdate(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void handleLocationUpdate(double latitude, double longitude) {
        new NetworkUtil(LocationTrackingService.this).updateLocation(latitude, longitude, new NetworkCallBack() {
            @Override
            public void onSuccess(int type, Object... object) {
                try {
                    Toast.makeText(LocationTrackingService.this, object[0].toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(LocationTrackingService.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}
