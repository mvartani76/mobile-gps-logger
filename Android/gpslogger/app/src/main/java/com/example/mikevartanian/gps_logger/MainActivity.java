package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mikevartanian.gps_logger.SettingsFragment;
import com.example.mikevartanian.gps_logger.LogFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    final SettingsFragment settingsFragment = new SettingsFragment();
    final LogFragment logFragment = new LogFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = settingsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_settings:
                    fragmentManager.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment;
                    return true;
                case R.id.navigation_log:
                    fragmentManager.beginTransaction().hide(active).show(logFragment).commit();
                    active = logFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager.beginTransaction().add(R.id.flContainer, logFragment, "log").hide(logFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContainer, settingsFragment, "settings").commit();

        SettingsDataViewModel mViewModel = ViewModelProviders.of(this).get(SettingsDataViewModel.class);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
