package com.location.locationtracking.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.location.locationtracking.R;
import com.location.locationtracking.utils.AppConstants;
import com.location.locationtracking.utils.LocationTracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBarColor();
        LocationTracking.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    permissionCheck();

                } else {
                    LaunchApp();
                }
            }
        }, 3000);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void permissionCheck() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("LOCATION");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "Please grant " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (Build.VERSION.SDK_INT >= 23) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            AppConstants.LOCATION_PERMISSION_CODE);
                                }

                            }
                        });
                return;
            }

            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        AppConstants.LOCATION_PERMISSION_CODE);
            }

            return;
        } else {
            // Toast.makeText(this,"Permission",Toast.LENGTH_LONG).show();
            LaunchApp();
        }

    }

    private boolean addPermission(List<String> permissionsList, String permission) {

        Boolean cond;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                if (!shouldShowRequestPermissionRationale(permission))
                    cond = false;
            }
            cond = true;
        } else {
            cond = true;
        }
        return cond;

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 23) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "LOcation Permission needed to run this App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == AppConstants.LOCATION_PERMISSION_CODE) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LaunchApp();

            } else {
                Toast.makeText(SplashActivity.this, "Location permission is required for this app", Toast.LENGTH_SHORT)
                        .show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, AppConstants.LOCATION_PERMISSION_CODE);
                        finish();
                    }
                }, 3000);
            }

        }
    }

    public void LaunchApp() {
        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(3000);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                } catch (Exception e) {

                }
            }
        };
        background.start();
    }
}
