package com.tabutech.recordmp4.Others;

import android.content.Context;
import android.preference.PreferenceManager;

import com.tabutech.recordmp4.R;


public class SharedPreferences {
    private static String PREF_HIGH_QUALITY = "pref_high_quality";

    public static void setPrefHighQuality(Context context,boolean isEnabled){
        android.content.SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        android.content.SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY, isEnabled);
        editor.apply();
    }

    public static boolean getPrefHighQuality(Context context){
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_HIGH_QUALITY,false);
    }


    public static void setBackgroundColor(Context context,int color){
       android.content.SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
       android.content.SharedPreferences.Editor editor = preferences.edit();
       editor.putInt("color",color);
       editor.apply();
    }

    public static int getSharedColor(Context context){
        android.content.SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("color",context.getResources().getColor(R.color.colorPrimary));
    }

    public static void setColorWord(Context context,String color){
        switch (color){
            case "Orange":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_orange));
                break;
            case "Black":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_Black));
                break;
            case "Brown":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_brown));
                break;
            case "Pink":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_pink));
                break;
            case "Red":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_red));
                break;
            case "Green":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_green));
                break;
            case "Grey":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_grey));
                break;
            case "Watery Blue":
                setBackgroundColor(context,context.getResources().getColor(R.color.pref_watery_blue));
                break;
            default:
                setBackgroundColor(context,context.getResources().getColor(R.color.colorPrimary));
                break;
        }
    }
}
