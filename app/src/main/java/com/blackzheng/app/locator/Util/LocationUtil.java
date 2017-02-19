package com.blackzheng.app.locator.Util;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by BlackZheng on 2017/2/18.
 */

public class LocationUtil {

    public static boolean isProviderEnbale(String providerName, LocationManager locationManager) {
        return locationManager.isProviderEnabled(providerName);
    }

    public static boolean hasPermission(Context context) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void requestSingleUpdate(Context context, LocationManager locationManager, String provider, LocationListener locationListener, ProgressDialog dialog) {
        if (isProviderEnbale(provider, locationManager)) {
            if (!hasPermission(context)) {
                Toast.makeText(context, "Location Permission denied!!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }
            locationManager.requestSingleUpdate(provider, locationListener, Looper.myLooper());
        }else{
            Toast.makeText(context, "Please enable " + provider + " provider!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }
}
