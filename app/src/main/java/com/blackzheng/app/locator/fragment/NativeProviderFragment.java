package com.blackzheng.app.locator.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blackzheng.app.locator.MainActivity;
import com.blackzheng.app.locator.R;
import com.blackzheng.app.locator.Util.DateUtil;
import com.blackzheng.app.locator.Util.FileUtil;
import com.blackzheng.app.locator.Util.LocationUtil;
import com.blackzheng.app.locator.Util.SharePrefUtil;
import com.blackzheng.app.locator.Util.TaskUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by BlackZheng on 2017/2/18.
 */

public class NativeProviderFragment extends Fragment {

    public static final String EXTRA_PROVIDER = "extra_provider";
    Button save, clear, start;
    TextView refer_provider, provider;
    TextView refer_longitude, longitude;
    TextView refer_latitude, latitude;
    TextView refer_accuracy, accuracy;
    TextView refer_time, time;
    TextView distanceTo;

    private String targetPath;
    private LocationManager locationManager;
    private Location reference = null;
    private Location location = null;
    private String mProvider;
    ProgressDialog dialog;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setupLocation(location, reference == null);
            save.setEnabled(reference != null);
            dialog.dismiss();
            if(timer != null)
                timer.cancel();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private Timer timer;

    public static NativeProviderFragment newInstance(String provider) {
        NativeProviderFragment fragment = new NativeProviderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PROVIDER, provider);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mProvider = bundle.getString(EXTRA_PROVIDER);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Locator", "onCreateView");
        View root = inflater.inflate(R.layout.native_location_fragment, container, false);
        parseArgument();
        save = (Button) root.findViewById(R.id.save);
        clear = (Button) root.findViewById(R.id.clear);
        start = (Button) root.findViewById(R.id.location);
        refer_provider = (TextView) root.findViewById(R.id.refer_provider);
        provider = (TextView) root.findViewById(R.id.provider);
        refer_latitude = (TextView) root.findViewById(R.id.refer_latitude);
        latitude = (TextView) root.findViewById(R.id.latitude);
        refer_longitude = (TextView) root.findViewById(R.id.refer_longitude);
        longitude = (TextView) root.findViewById(R.id.longitude);
        refer_accuracy = (TextView) root.findViewById(R.id.refer_accuracy);
        accuracy = (TextView) root.findViewById(R.id.accuracy);
        refer_time = (TextView) root.findViewById(R.id.refer_time);
        time = (TextView) root.findViewById(R.id.time);
        distanceTo = (TextView) root.findViewById(R.id.distance_to);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(getActivity(), "", getString(R.string.locating_message), true, false);
                LocationUtil.requestSingleUpdate(getContext(), locationManager, mProvider, locationListener, dialog);
                TimerTask cancelTask = new TimerTask() {
                    @Override
                    public void run() {
                        try{
                            locationManager.removeUpdates(locationListener);
                            dialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.timeout_message), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (SecurityException e){
                            e.printStackTrace();
                        }
                    }
                };
                //after 20 seconds timeouts
                timer = new Timer();
                timer.schedule(cancelTask, 20 * 1000);
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reference != null){
                    clearReference();
                    clear.setEnabled(false);
                    save.setEnabled(false);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetPath == null && reference != null){
                    saveReferenceAsyn(reference);
                }else{
                    if(location != null)
                        saveLocationAsyn(location);
                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Loading reference
        TaskUtils.executeAsyncTask(new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(getActivity(), "", "Loading", true, false);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                reference = loadReference();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if(reference != null) {
                    setupLocation(reference, true);
                    clear.setEnabled(true);
                }
                dialog.dismiss();
                super.onPostExecute(o);
            }
        });

    }

    private void setupLocation(Location location, boolean isReference){
        if(isReference){
            reference = location;
            refer_provider.setText(location.getProvider());
            refer_longitude.setText(location.getLongitude() + "");
            refer_latitude.setText(location.getLatitude() + "");
            refer_accuracy.setText(location.getAccuracy() + "");
            refer_time.setText(DateUtil.timeStamp2Date(location.getTime(), null));
        }else{
            this.location = location;
            provider.setText(location.getProvider());
            longitude.setText(location.getLongitude() + "");
            latitude.setText(location.getLatitude() + "");
            accuracy.setText(location.getAccuracy() + "");
            time.setText(DateUtil.timeStamp2Date(location.getTime(), null));
            if(reference != null)
                distanceTo.setText(location.distanceTo(reference) + "m");
        }
    }

    private Location loadReference(){
        Location reference = null;
        targetPath = SharePrefUtil.getInstance().getCurrentReferenceFileName(mProvider);
        Log.d("Locator", "targetPath = " + targetPath);
        if(targetPath != null){
            String json = FileUtil.readLine(MainActivity.defaultLogPath + targetPath, 7);
            try{
                reference = new Gson().fromJson(json, Location.class);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return reference;
    }

    private void saveReferenceAsyn(final Location location){
        TaskUtils.executeAsyncTask(new AsyncTask() {

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(getActivity(), "", "Saving", true, false);
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                saveReference(location);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                clear.setEnabled(true);
                dialog.dismiss();
                save.setEnabled(false);
                super.onPostExecute(o);
            }
        });
    }

    private void saveReference(Location location){

        targetPath = reference.getProvider() + "_" + DateUtil.timeStamp2Date(reference.getTime(), null) + ".txt";
        SharePrefUtil.getInstance().putCurrentRefernceFileName(mProvider, targetPath);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.reference) + "\n");
        sb.append(getString(R.string.provider) + location.getProvider() + "\n");
        sb.append(getString(R.string.latitude) + location.getLatitude() + "\n");
        sb.append(getString(R.string.longitude) + location.getLongitude() + "\n");
        sb.append(getString(R.string.accuracy) + location.getAccuracy() + "\n");
        sb.append(getString(R.string.time) + DateUtil.timeStamp2Date(location.getTime(), null) + "\n");
        sb.append(new Gson().toJson(location) + "\n");
        sb.append("\n");

        RandomAccessFile raf = null;
        try {
            File target = new File(MainActivity.defaultLogPath + targetPath);
            raf = new RandomAccessFile(target , "rw");
            raf.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearReference(){
        SharePrefUtil.getInstance().putCurrentRefernceFileName(mProvider, null);
        targetPath = null;
        reference = null;
        refer_provider.setText("");
        refer_latitude.setText("");
        refer_longitude.setText("");
        refer_accuracy.setText("");
        refer_time.setText("");
    }

    private void saveLocationAsyn(final Location location){
        TaskUtils.executeAsyncTask(new AsyncTask() {

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(getActivity(), "", "Saving", true, false);
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                saveLocation(location);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                dialog.dismiss();
                save.setEnabled(false);
                super.onPostExecute(o);
            }
        });
    }
    private void saveLocation(Location location){
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.provider) + location.getProvider() + "\n");
        sb.append(getString(R.string.latitude) + location.getLatitude() + "\n");
        sb.append(getString(R.string.longitude) + location.getLongitude() + "\n");
        sb.append(getString(R.string.accuracy) + location.getAccuracy() + "\n");
        sb.append(getString(R.string.time) + DateUtil.timeStamp2Date(location.getTime(), null) + "\n");
        sb.append(getString(R.string.distance_to) + location.distanceTo(reference) + "\n");
        sb.append(new Gson().toJson(location) + "\n");
        sb.append("\n");
        File target = new File(MainActivity.defaultLogPath + targetPath);
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(target , "rw");
            //将文件记录指针移动到最后
            raf.seek(target.length());
            // 输出文件内容
            raf.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
