package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment implements View.OnClickListener {
    private TextView tv, interval_label, interval_value;
    private Button logbutton, emailbutton;

    private OnFragmentInteractionListener mListener;
    private SettingsDataViewModel mViewModel;

    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

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
        interval_value.setText(String.valueOf(mViewModel.getLogInterval()));

        logbutton = (Button) view.findViewById(R.id.log_button);
        logbutton.setOnClickListener(this);
        emailbutton = (Button) view.findViewById(R.id.email_button);
        emailbutton.setOnClickListener(this);

        int tagStatus = mViewModel.getLogButtonState();

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
                final int tagStatus = (Integer) view.getTag();
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
        startTimer();
    }

    public void stopLogging() {
        stoptimertask(getView());
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 0ms the TimerTask will run every logInterval seconds
        timer.schedule(timerTask, 0, (long) mViewModel.getLogInterval());
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void initializeTimerTask() {
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
                        Toast toast = Toast.makeText(getActivity(), strDate, duration);
                        toast.show();
                    }
                });
            }
        };
    }
}
