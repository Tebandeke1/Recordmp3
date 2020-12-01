package com.tabutech.recordmp4.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tabutech.recordmp4.Others.RecordingFile;
import com.tabutech.recordmp4.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayBackFragment#} factory method to
 * create an instance of this fragment.
 */
public class PlayBackFragment extends DialogFragment {

    private static final String LOG_TAG = "PlaybackFragment";

    private static final String ARG_ITEM = "recording_item";

    private Handler handler = new Handler();

    private MediaPlayer mediaPlayer = null;

    private RecordingFile file;

    private SeekBar mSeekBar;
    private FloatingActionButton mPlayButton;
    private TextView mCurrentProgressView;
    private TextView mFileNameTextView;
    private TextView mFileLengthTextView;

    //stores if file is already playing
    private boolean isPlaying = false;

    //these stores minutes and seconds of the file

    long minutes = 0;
    long seconds = 0;



    public PlayBackFragment newInstance(RecordingFile file) {
        // Required empty public constructor
        PlayBackFragment f = new PlayBackFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM,file);
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = getArguments().getParcelable(ARG_ITEM);

        long duration = file.getmLength();

        minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toSeconds(minutes);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_play_back,null);

        mFileLengthTextView = view.findViewById(R.id.file_length_text_view);
        mFileNameTextView = view.findViewById(R.id.file_name_text_view);
        mCurrentProgressView = view.findViewById(R.id.file_progress_text_view);

        mSeekBar = view.findViewById(R.id.seekBar);

        ColorFilter filter = new LightingColorFilter(
                getResources().getColor(R.color.primary),getResources().getColor(R.color.primary));

        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(runnable);

                    long toMinutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
                    long second = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())
                            - TimeUnit.MINUTES.toSeconds(toMinutes);

                    mCurrentProgressView.setText(String.format("%02d:%02d",toMinutes,second));

                    updateSeekBar();

                }else if (mediaPlayer == null && fromUser){
                    prepareSeekBarFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null){
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer != null){
                    handler.removeCallbacks(runnable);

                    mediaPlayer.seekTo(mSeekBar.getProgress());

                    long minute = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
                    long second = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())
                            - TimeUnit.MINUTES.toSeconds(minute);

                    mCurrentProgressView.setText(String.format("%02d:%02d",minute,second));
                    updateSeekBar();
                }
            }
        });

        mPlayButton = view.findViewById(R.id.button_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaying(isPlaying);
                isPlaying = !isPlaying;
            }
        });

        mFileNameTextView.setText(file.getmName());
        mFileLengthTextView.setText(String.format("%02d:%02d",minutes,seconds));

        builder.setView(view);

        //request window without Title

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        //make the window dialogue transparent
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        //disable all the buttons
        AlertDialog alertDialog = (AlertDialog) getDialog();
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);

    }

    @Override
    public void onPause() {
        super.onPause();
        //when the activity or fragment is on pause
        if (mediaPlayer != null){
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //on destroying the activity or fragment
        if (mediaPlayer != null){
            stopPlaying();
        }
    }

    private void onPlaying(boolean isPlaying) {
        if (!isPlaying){
            //if the media is equal to null
            if (mediaPlayer == null){
                startPlaying();
            }else {
                //resume playing
                resumePlaying();
            }
        }else {
            //pause the media playing
            pausePlaying();
        }

    }
    private void startPlaying(){

        mPlayButton.setImageResource(R.drawable.ic_pause_button);
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getmFilePath());
            mediaPlayer.prepare();
            mSeekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }catch (Exception e){
            Log.e("Error","Preparing failed.");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();

        //the screen should stay awake while playing
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void stopPlaying() {

        mPlayButton.setImageResource(R.drawable.ic_play_button);
        handler.removeCallbacks(runnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        isPlaying = !isPlaying;

        mCurrentProgressView.setText(mFileLengthTextView.getText());

        mSeekBar.setProgress(mSeekBar.getMax());

        //keep the screen of after audio has stopped
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void pausePlaying(){
        mPlayButton.setImageResource(R.drawable.ic_pause_button);
        handler.removeCallbacks(runnable);
        mediaPlayer.pause();
    }

    private void resumePlaying(){
        mPlayButton.setImageResource(R.drawable.ic_pause_button);
        handler.removeCallbacks(runnable);
        mediaPlayer.start();
        updateSeekBar();
    }

    private void prepareSeekBarFromPoint(int progress) {

        //set media to play from the middle point

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getmFilePath());
            mediaPlayer.prepare();
            mSeekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.seekTo(progress);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        }catch (IOException e){
            Log.e("Error","Failed to prepared");
        }

        updateSeekBar();

        //make sure the screen is on while audio is playing
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    //update the seekBar
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null){
                int currentPosition = mediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(currentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())
                        - TimeUnit.MINUTES.toSeconds(minutes);

                mCurrentProgressView.setText(String.format("%02d:%02d",minutes,seconds));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        handler.postDelayed(runnable,1000);

    }
}