package com.tabutech.recordmp4.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;

import com.tabutech.recordmp4.Others.SharedPreferences;
import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.fragments.Settings_Fragment;

public class SettingsActivity extends AppCompatActivity {

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (SharedPreferences.getSharedColor(this) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
        }


        if (actionBar != null){
            actionBar.setTitle(R.string.settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new Settings_Fragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferences.getSharedColor(this) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (SharedPreferences.getSharedColor(this) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
        }

    }
}