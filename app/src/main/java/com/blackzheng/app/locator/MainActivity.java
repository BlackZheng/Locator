package com.blackzheng.app.locator;

import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blackzheng.app.locator.Util.SharePrefUtil;
import com.blackzheng.app.locator.fragment.NativeProviderFragment;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static String defaultLogPath;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            defaultLogPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getCanonicalPath() + "/";
            Log.d("path", defaultLogPath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "can't get log path", Toast.LENGTH_LONG).show();
            finish();
        }
        FragmentPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        providers = new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return providers[position];
        }

        @Override
        public Fragment getItem(int position) {
            return NativeProviderFragment.newInstance(providers[position]);
        }

        @Override
        public int getCount() {
            return providers.length;
        }
    }
}
