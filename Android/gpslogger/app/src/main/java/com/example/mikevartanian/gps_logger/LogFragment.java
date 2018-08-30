package com.example.mikevartanian.gps_logger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment implements View.OnClickListener {
    private TextView tv;
    private Button logbutton, emailbutton;

    private OnFragmentInteractionListener mListener;

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

        logbutton = (Button) view.findViewById(R.id.log_button);
        logbutton.setOnClickListener(this);
        emailbutton = (Button) view.findViewById(R.id.email_button);
        emailbutton.setOnClickListener(this);
        logbutton.setTag(1);
        logbutton.setText("Start Logging");

        Log.i(TAG, "LogFragment OnCreateView Called");
        SettingsDataViewModel mViewModel = ViewModelProviders.of(this).get(SettingsDataViewModel.class);

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
                } else {
                    logbutton.setText("Start Logging");
                    view.setTag(1);
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
}
