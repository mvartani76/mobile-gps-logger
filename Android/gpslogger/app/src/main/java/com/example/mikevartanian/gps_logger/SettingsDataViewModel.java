package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsDataViewModel extends ViewModel {

    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";
    public int logButtonState = 1;

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

    public void startTimer(long logInterval, Context currentActivity) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask(currentActivity);

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

    public void initializeTimerTask(final Context currentActivity) {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(currentActivity, strDate, duration);
                        toast.show();
                    }
                });
            }
        };
    }
}