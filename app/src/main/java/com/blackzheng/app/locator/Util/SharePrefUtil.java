package com.blackzheng.app.locator.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import com.blackzheng.app.locator.LocatorApplication;

/**
 * Created by BlackZheng on 2017/2/18.
 */

public class SharePrefUtil {

    private static SharePrefUtil mInstance;

    public static final String REFERENCE_INFO="reference_info";
    public static final String GPS_CURRENT_REFERENCE="gps_current_reference";
    public static final String NETWORK_CURRENT_REFERENCE="network_current_reference";
    public static final String AMAP_CURRENT_REFERENCE="amap_current_reference";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static SharePrefUtil getInstance(){
        if (mInstance == null) {
            synchronized (SharePrefUtil.class) {
                if (mInstance == null) {
                    mInstance = new SharePrefUtil(LocatorApplication.getContext(), REFERENCE_INFO);
                }
            }
        }
        return mInstance;
    }

    private SharePrefUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    private String getGpsCurrentReferenceFileName(){
        return sp.getString(GPS_CURRENT_REFERENCE, null);
    }

    private void putGpsCurrentReferenceFileName(String fileName){
        editor.putString(GPS_CURRENT_REFERENCE, fileName);
        editor.commit();
    }

    private String getNetworkCurrentReferenceFileName(){
        return sp.getString(NETWORK_CURRENT_REFERENCE, null);
    }

    private void putNetworkCurrentReferenceFileName(String fileName){
        editor.putString(NETWORK_CURRENT_REFERENCE, fileName);
        editor.commit();
    }

    private String getAmapCurrentReferenceFileName(){
        return sp.getString(AMAP_CURRENT_REFERENCE, null);
    }

    private void putAmapCurrentReferenceFileName(String fileName){
        editor.putString(AMAP_CURRENT_REFERENCE, fileName);
        editor.commit();
    }

    public String getCurrentReferenceFileName(String provider){

        switch (provider){
            case LocationManager.GPS_PROVIDER:
                return getGpsCurrentReferenceFileName();
            case LocationManager.NETWORK_PROVIDER:
                return getNetworkCurrentReferenceFileName();
            default:
                return null;
        }

    }

    public void putCurrentRefernceFileName(String provider, String fileName){
        switch (provider){
            case LocationManager.GPS_PROVIDER:
                putGpsCurrentReferenceFileName(fileName);
            case LocationManager.NETWORK_PROVIDER:
                putNetworkCurrentReferenceFileName(fileName);
            default:
                return;
        }
    }

}
