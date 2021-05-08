package com.tabutech.recordmp4.Others;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.midi.MidiDeviceInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.tabutech.recordmp4.DatabaseListener.OnDatabaseChangedListener;
import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.activities.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service {

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mediaRecorder = null;

    private DBHelper mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimeChangedListener onTimeChangedListener = null;
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimeChangedListener{
        void onTimeChanged(int seconds);
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

        }else {
            startRecording();
        }
       // startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (mediaRecorder != null){
            stopRecording();
        }
        super.onDestroy();
    }

    private void startRecording() {

        setFileNameAndPath();
        mediaRecorder = new MediaRecorder();
       // mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        if (SharedPreferences.getPrefHighQuality(this)){
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(19200);
        }

        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFileNameAndPath() {

        int count = 0;

        File f;

        do {
            count++;

            mFileName = getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".mp3";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/RecordMp3/" + mFileName;

            f = new File(mFilePath);

        }while (f.exists() && !f.isDirectory());
    }


    public void stopRecording(){
        try {
            mediaRecorder.stop();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mediaRecorder.release();
            Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        }catch (IllegalStateException e){
            e.printStackTrace();
        }

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mediaRecorder = null;

        try {
            mDatabase.addRecordings(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startTimer(){
        mTimer = new Timer();

        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;

                if (onTimeChangedListener != null){
                    onTimeChangedListener.onTimeChanged(mElapsedSeconds);
                }
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(5622,notification());

            }
        };

        mTimer.scheduleAtFixedRate(mIncrementTimerTask,1000,1000);
    }

    private Notification notification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_baseline_eddy_mic)
                .setContentTitle(getString(R.string.notification_recording))
                .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                .setOngoing(true);


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}
