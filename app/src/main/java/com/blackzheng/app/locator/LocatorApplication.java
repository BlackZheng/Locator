package com.blackzheng.app.locator;

import android.app.Application;
import android.content.Context;

/**
 * Created by BlackZheng on 2017/2/19.
 */

public class LocatorApplication extends Application {

    public static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext(){
        return sContext;
    }
}
