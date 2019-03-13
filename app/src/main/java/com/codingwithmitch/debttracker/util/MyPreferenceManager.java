package com.codingwithmitch.debttracker.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferenceManager {

    public static final String ORDER_BY_NEWEST = "com.codingwithmitch.debttracker.order_by_newest";
    public static final String SHOW_ONLY_SETTLED = "com.codingwithmitch.debttracker.show_only_settled";

    private static MyPreferenceManager instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static MyPreferenceManager getInstance(Context context){
        if(instance == null){
            instance = new MyPreferenceManager(context);
        }
        return instance;
    }

    private MyPreferenceManager(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setOrderByNewest(boolean bool){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ORDER_BY_NEWEST, bool);
        editor.apply();
    }

    public void setOnlySettled(boolean bool){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_ONLY_SETTLED, bool);
        editor.apply();
    }

    public boolean getShowOnlySettled(){
        return preferences.getBoolean(SHOW_ONLY_SETTLED, false);
    }

    public boolean getOrderByNewest(){
        return preferences.getBoolean(ORDER_BY_NEWEST, true);
    }
}
