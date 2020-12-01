package com.tabutech.recordmp4.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.tabutech.recordmp4.Others.SharedPreferences;
import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.adapters.MyFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    //splash screen time out
    public static final int SPLASH_SCREEN_OUT_TIME = 2000;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pager = findViewById(R.id.pager);

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(),getApplicationContext());

        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(pager);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        if (toolbar != null){
            setSupportActionBar(toolbar);
        }
        if (SharedPreferences.getSharedColor(getApplicationContext()) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));

        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (SharedPreferences.getSharedColor(getApplicationContext()) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }
    }

    protected void onResume(){
        super.onResume();
        if (SharedPreferences.getSharedColor(getApplicationContext()) != getResources().getColor(R.color.colorPrimary)){
            toolbar.setBackgroundColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(SharedPreferences.getSharedColor(this));
            tabLayout.setBackgroundColor(SharedPreferences.getSharedColor(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //this add menu icon to the tool bar if it exists
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.share_btn:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("Text/plain");
                share.putExtra(share.EXTRA_SUBJECT,"App");
                startActivity(share.createChooser(share,"Share app using.."));
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }

    }
}