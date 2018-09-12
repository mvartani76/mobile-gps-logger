package com.example.mikevartanian.gps_logger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsDataViewModel extends ViewModel {

    public static final int REQUEST_LOCATION_PERMISSIONS = 1234;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 2345;
    public String logFormat = "gpx";
    public int logInterval = 300;
    public String logMethod = "Time";
    public int formatSpinnerState = 0;
    public int logButtonState = 1;
    public String logLocationProvider = LocationManager.NETWORK_PROVIDER;

    public String XMLString = "";
    public LatLonPair latlon;

    public static LocationManager locationManager;
    public static LocationListener locationListener;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    public String getLogFormat() {
        return this.logFormat;
    }

    public int getformatSpinnerState() {
        return this.formatSpinnerState;
    }

    // If logMethod = Time, get the logInterval time in milliseconds
    // As the slider/datamodel stores in seconds, need
    // to multiply by 1000
    public int getLogInterval(String units) {
        if (units == "msec") {
            return this.logInterval * 1000;
        }
        else {
            return this.logInterval;
        }
    }

    public String getLogMethod() {
        return this.logMethod;
    }

    public int getLogButtonState() {
        return this.logButtonState;
    }

    public String getLogLocationProvider() {
        return this.logLocationProvider;
    }

    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    public void setformatSpinnerState(int formatSpinnerState) {
        this.formatSpinnerState = formatSpinnerState;
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

    public void setLogLocationProvider(String logLocationProvider) {
        this.logLocationProvider = logLocationProvider;
    }

    // logInterval should be in milliseconds as timer.schedule requires milliseconds
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        latlon = getLastKnownLocation(currentActivity, lat_textview, lon_textview);

                        XMLString = writeDataToString(logFormat, String.valueOf(latlon.returnLat()), String.valueOf(latlon.returnLon()), strDate, XMLString);

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(currentActivity, strDate, duration);
                        toast.show();
                    }
                });
            }
        };
    }

    @SuppressLint("MissingPermission")
    public LatLonPair getLastKnownLocation(Context currentContext, TextView lat_textview, TextView lon_textview) {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
        double lat = 0, lon = 0;

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (locationManager.isProviderEnabled(logLocationProvider)) {

                Location lastKnownLocation = locationManager.getLastKnownLocation(logLocationProvider);

                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                lat = lastKnownLocation.getLatitude();
                lon = lastKnownLocation.getLongitude();
        }

        // write the lat/lon values to a textview
        lat_textview.setText(String.valueOf((lat)));
        lon_textview.setText(String.valueOf((lon)));

        return new LatLonPair(lat, lon);
    }

    public void startLocationUpdates(final Context currentContext, String logLocationProvider, long logInterval, final TextView lat_textview, final TextView lon_textview) {
        String TAG = "Start Location Update";
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);

        //get the current timeStamp
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String strDate = simpleDateFormat.format(calendar.getTime());

        try {
            //currentContext.checkPermission()
            locationManager.requestLocationUpdates(
                    logLocationProvider, 1000, logInterval, new LocationListener() {
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }

                        @Override
                        public void onLocationChanged(Location location) {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(currentContext, strDate, duration);
                            toast.show();

                            // write the lat/lon values to a textview
                            lat_textview.setText(String.valueOf((location.getLatitude())));
                            lon_textview.setText(String.valueOf((location.getLongitude())));

                            XMLString = writeDataToString(logFormat, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), strDate, XMLString);
                            locationListener = this;
                        }
                    });
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    public String startXMLString(String logFormat) {
        String inputXMLString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";

        switch (logFormat) {
            case "gpx":
                inputXMLString = inputXMLString + "\t<gpx version=\"1.1\" creator=\"Xcode\">\n";
                break;
            case "kml":
                inputXMLString = inputXMLString + "\t<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";
                inputXMLString = inputXMLString + "\t\t<Placemark>\n";
                inputXMLString = inputXMLString + "\t\t\t<name>iOS Test Path</name>\n";
                inputXMLString = inputXMLString + "\t\t\t<LineString>\n";
                inputXMLString = inputXMLString + "\t\t\t\t<tessellate>1</tessellate>\n";
                inputXMLString = inputXMLString + "\t\t\t\t<coordinates>\n";
                break;
            default:
                break;
            }

        return inputXMLString;
    }

    public String writeDataToString(String logFormat, String lat, String lon, String utcTimeZoneStr, String stringName) {
        String inputXMLString = stringName;

        switch (logFormat) {
            case "gpx":
                inputXMLString = inputXMLString + "\t\t<wpt lat=\"" + lat + "\" lon=\"" + lon + "\"></wpt>\n";
                inputXMLString = inputXMLString + "\t\t<time>" + utcTimeZoneStr + "</time>\n";
                //inputXMLString = inputXMLString + "\t\t<metadata><keywords>" + stateStr + "</keywords></metadata>\n";
                break;
            case "kml":
                inputXMLString = inputXMLString + "\t\t\t\t\t" + lon + "," + lat + "," + "0\n";
                break;
            default:
                break;
        }
        return inputXMLString;
    }

    // Finalize the XML String
    public String finishDataWriteToString(String logFormat, String stringName) {
        String inputXMLString = stringName;

        switch (logFormat) {
            case "gpx":
                inputXMLString = inputXMLString + "\t</gpx>\n" + "</xml>";
                break;
            case "kml":
                inputXMLString = inputXMLString + "\t\t\t\t</coordinates>\n";
                inputXMLString = inputXMLString + "\t\t\t</LineString>\n";
                inputXMLString = inputXMLString + "\t\t</Placemark>\n";
                inputXMLString = inputXMLString + "\t</kml>\n" + "</xml>";
                break;
            default:
                break;
        }
        return inputXMLString;
    }
}

final class LatLonPair {
    private final double lat;
    private final double lon;

    public LatLonPair(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double returnLat() {
        return lat;
    }

    public double returnLon() {
        return lon;
    }
}