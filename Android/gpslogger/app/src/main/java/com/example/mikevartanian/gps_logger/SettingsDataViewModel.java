package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModel;

public class SettingsDataViewModel extends ViewModel {

    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";
    public int logButtonState = 1;

    public String getLogFormat() {
        return this.logFormat;
    }

    public int getLogInterval() {
        return this.logInterval;
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