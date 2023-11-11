package com.example.socialmediasmall;

import android.content.Context;

import java.util.Set;

public class DataLocalManager {
    private static final String PREF_FIRST = "PREF_FIRST";
    private MySharedReference mySharedReference;
    private static DataLocalManager instance;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedReference = new MySharedReference(context);

    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setStringValue(Set<String> value) {
        DataLocalManager.getInstance().mySharedReference.setStringValue(PREF_FIRST,
                value);
    }

    public static Set<String> getStringValue() {
        return DataLocalManager.getInstance().mySharedReference.getStringSetValue(PREF_FIRST);
    }

}
