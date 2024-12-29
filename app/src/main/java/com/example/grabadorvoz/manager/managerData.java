package com.example.grabadorvoz.manager;

import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.KEY_USER;

import android.content.Context;
import android.content.SharedPreferences;


public class managerData {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public managerData(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveString(String key, String data) {
        editor.putString(key, data);
        editor.apply();
    }

    public void saveInt(String key, int data) {
        editor.putInt(key, data);
        editor.apply();
    }

    public void saveBoolean(String key, boolean data) {
        editor.putBoolean(key, data);
        editor.apply();
    }

    public void saveFloat(String key, float data) {
        editor.putFloat(key, data);
        editor.apply();
    }

    public void saveLong(String key, long data) {
        editor.putLong(key, data);
        editor.apply();
    }

    // Métodos para obtener datos
    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public String getStringDate(String key) {
        return sharedPreferences.getString(key, "n");
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public boolean getBooleanNotidication(String key) {
        return sharedPreferences.getBoolean(key, true);
    }


    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0.0f);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }


    public void clear() {
        editor.clear();
        editor.apply();
    }
}

