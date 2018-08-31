package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsDataViewModel extends ViewModel {

    public static final int REQUEST_LOCATION_PERMISSIONS = 1234;
    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";
    public int logButtonState = 1;

    String locationProvider = LocationManager.NETWORK_PROVIDER;

    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public String getLogFormat() {
        return this.logFormat;
    }

    // Get the logInterval time in milliseconds
    // As the slider/datamodel stores in seconds, need
    // to multiply by 1000
    public int getLogInterval() {
        return this.logInterval*1000;
    }

    public String getLogMethod() {
        return this.logMethod;
    }

    public int getLogButtonState() {
        return this.logButtonState;
    }

    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    public void setLogInterval(int logInterval) {
        this.logInterval = logInterval;
    }

    public void setLogMethod(String logMethod) {
        this.logMethod = logMethod;
    }

    public void setLogButtonState(int logButtonState) {
        this.logButtonState = logButtonState;
    }

    public void startTimer(long logInterval, Context currentActivity, TextView lat_textview, TextView lon_textview) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask(currentActivity, lat_textview, lon_textview);

        //schedule the timer, after the first 0ms the TimerTask will run every logInterval seconds
        timer.schedule(timerTask, 0, logInterval);
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void initializeTimerTask(final Context currentActivity, final TextView lat_textview, final TextView lon_textview) {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        getLastKnownLocation(currentActivity, lat_textview, lon_textview);

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(currentActivity, strDate, duration);
                        toast.show();
                    }
                });
            }
        };
    }
    public void getLastKnownLocation(Context currentContext, TextView lat_textview, TextView lon_textview){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double lat = lastKnownLocation.getLatitude();
        double lon = lastKnownLocation.getLongitude();

        // write the lat/lon values to a textview
        lat_textview.setText(String.valueOf((lat)));
        lon_textview.setText(String.valueOf((lon)));
    }
}