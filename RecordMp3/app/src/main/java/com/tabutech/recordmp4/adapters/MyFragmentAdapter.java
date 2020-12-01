package com.tabutech.recordmp4.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.fragments.FileViewerFragment;
import com.tabutech.recordmp4.fragments.RecordFragment;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private String titles[] = {String.valueOf(R.string.tab_title_record),String.valueOf(R.string.tab_title_saved_recordings)};

    private Context context;
    public MyFragmentAdapter(@NonNull FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: {
                return new RecordFragment().newInstance(position);
            }
            case 1:{
                return new FileViewerFragment().newInstance(position);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
     switch (position){
         case 0:
             return context.getResources().getString(R.string.tab_title_record);
         case 1:
             return context.getResources().getString(R.string.tab_title_saved_recordings);
     }
     return null;
    }
}
