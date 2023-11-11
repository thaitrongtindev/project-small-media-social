package com.example.socialmediasmall;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class MySharedReference {

    private static final String MY_SHARED_PREFENCES = "MY_SHARED_PREFENCES";
    private Context mContext;

    public MySharedReference(Context mContext) {
        this.mContext = mContext;
    }
    public void setStringValue(String key, Set<String> value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARED_PREFENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public Set<String> getStringSetValue(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARED_PREFENCES,
                Context.MODE_PRIVATE);
        Set<String> valueDefault = new HashSet<>();
        return sharedPreferences.getStringSet(key, valueDefault);
    }
}
