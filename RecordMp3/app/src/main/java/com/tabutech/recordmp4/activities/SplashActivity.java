package com.tabutech.recordmp4.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.tabutech.recordmp4.Others.SharedPreferences;
import com.tabutech.recordmp4.R;

import static com.tabutech.recordmp4.activities.MainActivity.SPLASH_SCREEN_OUT_TIME;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },SPLASH_SCREEN_OUT_TIME);

        ConstraintLayout layout = findViewById(R.id.constraint_lay);
        layout.setBackgroundColor(SharedPreferences.getSharedColor(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
        }
    }
}