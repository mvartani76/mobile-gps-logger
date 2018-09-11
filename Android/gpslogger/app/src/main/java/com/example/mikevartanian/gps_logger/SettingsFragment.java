package com.example.mikevartanian.gps_logger;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private TextView tv;
    private Spinner spinner;
    private SeekBar logIntervalSeekBar;
    private EditText logIntervalEditText;
    private TextView logIntervalTextView;
    private SettingsDataViewModel mViewModel;
    private RadioGroup dataLogIntervalGroup;
    private RadioButton timeIntMethodButton;
    private RadioButton distanceIntMethodButton;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String TAG = "MCV Logs";

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        tv = (TextView) view.findViewById(R.id.tv);
        tv.setText("Settings");

        logIntervalSeekBar = (SeekBar) view.findViewById(R.id.loginterval_seekbar);
        logIntervalEditText = (EditText) view.findViewById(R.id.loginterval_edittext);
        logIntervalTextView = (TextView) view.findViewById(R.id.loginterval_textview_label);

        Log.i(TAG, "SettingsFragment OnCreateView Called");

        dataLogIntervalGroup = (RadioGroup) view.findViewById(R.id.radiogroup_loginterval);
        timeIntMethodButton = (RadioButton) view.findViewById(R.id.radio_time);
        distanceIntMethodButton = (RadioButton) view.findViewById(R.id.radio_distance);

        spinner = (Spinner) view.findViewById(R.id.fileformat_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.fileformat_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        mViewModel = ViewModelProviders.of(getActivity()).get(SettingsDataViewModel.class);

        dataLogIntervalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_time:
                        mViewModel.setLogFormat("Time");
                        logIntervalTextView.setText("Interval (s) = ");
                        break;
                    case R.id.radio_distance:
                        mViewModel.setLogFormat("Distance");
                        logIntervalTextView.setText("Interval (m) = ");
                        break;
                }
            }
        });

        // Initialize the textview with '0'.
        logIntervalEditText.setText(String.valueOf(logIntervalSeekBar.getProgress()));

        logIntervalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                Toast.makeText(getActivity(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                mViewModel.setLogInterval(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                logIntervalEditText.setText(String.valueOf(progress));
                Toast.makeText(getActivity(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
                mViewModel.setLogInterval(progress);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private static final String TAG = "MCV Logs";
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Log.i(TAG, "spinner.pos = " + pos);

                // Set the log format based on the spinner position
                // Do not like using integers for gpx/kml but not sure how to get
                // the string array locations yet
                switch (pos) {
                    case 0:
                        mViewModel.setLogFormat("gpx");
                        break;
                    case 1:
                        mViewModel.setLogFormat("kml");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.i(TAG, "Nothing Selected");
            }

        });

        logIntervalEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        logIntervalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    logIntervalSeekBar.setProgress(Integer.parseInt((logIntervalEditText.getText().toString())));

                    Activity kBActivity = getActivity();
                    View view = kBActivity.getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(kBActivity);
                    }

                    InputMethodManager imm = (InputMethodManager) kBActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });


        return view;
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

        Log.i(TAG, "SettingsFragment OnDestroyView Called");
    }
}