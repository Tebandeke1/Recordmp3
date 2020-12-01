package com.tabutech.recordmp4.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tabutech.recordmp4.Others.RecordingService;
import com.tabutech.recordmp4.Others.SharedPreferences;
import com.tabutech.recordmp4.R;

import java.io.File;


public class RecordFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;

    //recording utils
    private Chronometer mChronometer = null;
    private TextView mRecordingStatus;
    private FloatingActionButton mStartRecordingButton;
    private Button mPauseButton;
    private boolean isRecording = true;
    private  boolean isPaused = true;

    private int mRecordingPrompt = 0;

    //records time when paused
    private long timeWhenPaused =0;

    private Context context;

    public RecordFragment() {
        // Required empty public constructor

    }

    /**
     * A simple {@link Fragment} subclass.
     * Use the  factory method to
     * create an instance of this fragment.
     */

    public RecordFragment newInstance(int position){
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION,position);
        f.setArguments(b);

        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        //mStartRecordingButton.setBackgroundColor(context.getResources().getColor(SharedPreferences.getSharedColor(context)));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View recordView  = inflater.inflate(R.layout.fragment_record, container, false);

        //initializing data view
        mChronometer = recordView.findViewById(R.id.chronometer);

        mStartRecordingButton = recordView.findViewById(R.id.btnRecord);
        mStartRecordingButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        mStartRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording(isRecording);
                isRecording = !isRecording;
            }
        });

        mRecordingStatus = recordView.findViewById(R.id.recording_status);

        mPauseButton = recordView.findViewById(R.id.btn_pause);
        mPauseButton.setVisibility(View.GONE);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseRecording(isPaused);
                isPaused  = !isPaused;
            }
        });
        return recordView;
    }

    //when recording has been paused or stopped
    private void pauseRecording(boolean isPaused) {

        if (isPaused){
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_play,0,0,0);

            mPauseButton.setVisibility(View.INVISIBLE);
            mRecordingStatus.setText(getActivity().getString(R.string.resume_recording_button).toUpperCase());

            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();

            mChronometer.start();
        }else {
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause,0,0,0);

            mRecordingStatus.setText(getString(R.string.pause_recording_button).toUpperCase());

            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);

            mChronometer.stop();
        }

    }

    //start recording and pause
    private void startRecording(boolean isRecording) {
        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (isRecording){

            //if recording is started

            mPauseButton.setVisibility(View.VISIBLE);
            mStartRecordingButton.setImageResource(R.drawable.ic_stop_recording);
            Toast.makeText(getActivity(), R.string.start_record_toast, Toast.LENGTH_SHORT).show();
            //saving file
            File folder = new File(Environment.getExternalStorageDirectory()+"/RecordMp3");

            //if the folder does not exist ,create a new one

            if (!folder.exists()){
                folder.mkdir();
            }

            //set chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordingPrompt == 0){
                        mRecordingStatus.setText(getActivity().getString(R.string.recording_status_pro)+"'");
                    }else if (mRecordingPrompt == 1){
                        mRecordingStatus.setText(getActivity().getString(R.string.record_in_progress)+"..");
                    }else if (mRecordingPrompt == 2){
                        mRecordingStatus.setText(getActivity().getString(R.string.record_in_progress)+"...");
                        mRecordingPrompt = -1;
                    }
                    mRecordingPrompt++;
                }
            });

            //start service here
            getActivity().startService(intent);

            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingStatus.setText(getActivity().getString(R.string.record_in_progress)+".");

        }else {

            mStartRecordingButton.setImageResource(R.drawable.ic_white_mic);

            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingStatus.setText(getActivity().getString(R.string.record_status));
            //start service
            getActivity().stopService(intent);

            //allow screen to go of while recording has stopped

            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        }


    }
}