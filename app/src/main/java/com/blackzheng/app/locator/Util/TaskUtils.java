package com.blackzheng.app.locator.Util;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by BlackZheng on 2017/2/19.
 */

public class TaskUtils {
    public static  void executeAsyncTask(AsyncTask task) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
