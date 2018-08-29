package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModel;

public class SettingsDataViewModel extends ViewModel {

    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";

}