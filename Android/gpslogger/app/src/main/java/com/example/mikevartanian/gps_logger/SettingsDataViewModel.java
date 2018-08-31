package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;

public class SettingsDataViewModel extends ViewModel {

    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";
    public int logButtonState = 1;

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
}