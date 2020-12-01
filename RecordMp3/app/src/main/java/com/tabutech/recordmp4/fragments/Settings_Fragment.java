package com.tabutech.recordmp4.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.tabutech.recordmp4.BuildConfig;
import com.tabutech.recordmp4.Others.SharedPreferences;
import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.activities.SettingsActivity;


public class Settings_Fragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference checkBoxPreference = (CheckBoxPreference)findPreference(getResources().getString(R.string.pref_high_quality_key));
        checkBoxPreference.setChecked(SharedPreferences.getPrefHighQuality(getActivity()));
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.setPrefHighQuality(getActivity(),(boolean)newValue);
                return true;
            }
        });


        Preference about = findPreference(getString(R.string.pref_about_key));
        about.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LicenceFragment fragment = new LicenceFragment();

                fragment.show(((SettingsActivity)getActivity())
                        .getSupportFragmentManager()
                        .beginTransaction(),"dialogue Licence");
                return true;
            }
        });


        final ListPreference listPreference = (ListPreference)findPreference(getResources().getString(R.string.list_colors));

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.setColorWord(getActivity(),newValue.toString());
                Toast.makeText(getActivity(), "You have changed app color to "+newValue.toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
                getActivity().finish();

                return true;
            }
        });
    }
}