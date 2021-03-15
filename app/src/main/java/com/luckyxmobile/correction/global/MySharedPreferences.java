package com.luckyxmobile.correction.global;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final MySharedPreferences single = new MySharedPreferences();

    private MySharedPreferences() { }

    public static MySharedPreferences getInstance() {
        return single;
    }


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public void init(Application application) {
        preferences = application.getSharedPreferences(Constants.TABLE_CORRECTION, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public void putBoolean(String key,boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void clearAll() {
        editor.clear();
        editor.apply();
    }

    public void clear(String key) {
        editor.remove(key);
        editor.apply();
    }
}
