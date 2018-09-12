package com.example.mikevartanian.gps_logger;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment implements View.OnClickListener {
    private TextView tv, interval_label, interval_value, lat_value, lon_value;
    private Button logbutton, emailbutton;
    String email, subject, message, attachmentFile;
    Uri URI = null;
    private FileWriter writer;

    private OnFragmentInteractionListener mListener;
    private SettingsDataViewModel mViewModel;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String TAG = "MCV Logs";

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        tv = (TextView) view.findViewById(R.id.tv);
        tv.setText("Log");
        interval_label = (TextView) view.findViewById(R.id.interval_label);
        interval_value = (TextView) view.findViewById(R.id.interval_value);

        mViewModel = ViewModelProviders.of(getActivity()).get(SettingsDataViewModel.class);
        interval_value.setText(String.valueOf(mViewModel.getLogInterval("sec")));

        logbutton = (Button) view.findViewById(R.id.log_button);
        logbutton.setOnClickListener(this);
        emailbutton = (Button) view.findViewById(R.id.email_button);
        emailbutton.setOnClickListener(this);

        lat_value = (TextView) view.findViewById(R.id.lat_value);
        lon_value = (TextView) view.findViewById(R.id.lon_value);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SettingsDataViewModel.REQUEST_LOCATION_PERMISSIONS);
        } else {
            LatLonPair latlon = mViewModel.getLastKnownLocation(getActivity(), lat_value, lon_value);
        }

        int tagStatus = mViewModel.getLogButtonState();

        if (mViewModel.getLogMethod() == "Time") {
            interval_label.setText("Interval (s) = ");
        } else {
            interval_label.setText("Interval (m) = ");
        }

        logbutton.setTag(tagStatus);
        if (tagStatus == 1) {
            logbutton.setText("Start Logging");
        } else {
            logbutton.setText("Stop Logging");
        }

        Log.i(TAG, "LogFragment OnCreateView Called");

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.log_button:
                int tagStatus = (Integer) view.getTag();
                if (tagStatus == 1) {
                    logbutton.setText("Stop Logging");
                    view.setTag(0);
                    mViewModel.setLogButtonState(0);
                    startLogging();
                } else {
                    logbutton.setText("Start Logging");
                    view.setTag(1);
                    mViewModel.setLogButtonState(1);
                    stopLogging();
                }
                break;
            case R.id.email_button:
                // Get the state of the log button
                // We only want to email the logs after the app has stopped logging data
                tagStatus = mViewModel.getLogButtonState();
                if (tagStatus == 1) {

                    //get the current timeStamp
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getDefault());
                    final String strDate = simpleDateFormat.format(calendar.getTime());

                    String filename = "log";
                    if (mViewModel.getLogFormat() == "gpx") {
                        filename = filename + strDate + ".gpx";
                    } else if (mViewModel.getLogFormat() == "kml") {
                        filename = filename + strDate + ".kml";
                    }

                    // Write the data string to a file and email it
                    emailFile(writeToFile(getActivity(), mViewModel.XMLString, filename));

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Logs Emailed", duration);
                    toast.show();
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Stop Logs First", duration);
                    toast.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        String TAG = "MCV Logs";

        Log.i(TAG, "LogFragment OnDestroyView Called");
    }

    public void startLogging() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SettingsDataViewModel.REQUEST_LOCATION_PERMISSIONS);
        } else {
            mViewModel.XMLString = mViewModel.startXMLString(mViewModel.logFormat);
            if (mViewModel.getLogMethod() == "Time") {
                // startTimer requires logInterval to be in milliseconds
                mViewModel.startTimer((long) mViewModel.getLogInterval("msec"), getActivity(), lat_value, lon_value);
            }
            else {
                mViewModel.startLocationUpdates(getActivity(), mViewModel.getLogLocationProvider(), mViewModel.getLogInterval("meters"), lat_value, lon_value);
            }
        }
    }

    public void stopLogging() {
        if (mViewModel.getLogMethod() == "Time") {
            mViewModel.stoptimertask(getView());
        } else {
            mViewModel.stopLocationUpdates();
        }
        mViewModel.XMLString = mViewModel.finishDataWriteToString(mViewModel.logFormat, mViewModel.XMLString);
    }

    private File writeToFile(Context context, String data, String filename) {
        File file = null;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SettingsDataViewModel.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
        } else {
            try {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

                writer = new FileWriter(file);
                writer.write(data);
                writer.close();
                Toast.makeText(getActivity(), "Temporarily saved contents in " + file.getPath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Unable create temp file. Check logcat for stackTrace", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return file;
        }
        return file;
    }

    private void emailFile(File fileToSend) {
        try {
            email = "mikev@digital2go.com";
            subject = "Android Device GPS Logs";
            message = "Please see attached log file.";

            final Intent emailIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
            new String[] { email });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileToSend.getAbsoluteFile()));

            this.startActivity(Intent.createChooser(emailIntent,
                    "Sending email..."));
        } catch (Throwable t) {
            Toast.makeText(getActivity(), "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }
}