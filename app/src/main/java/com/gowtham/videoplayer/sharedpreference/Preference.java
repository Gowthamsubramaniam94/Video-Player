package com.gowtham.videoplayer.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class Preference {

    private SharedPreferences mySharedPreference;
    private SharedPreferences.Editor mySharedEditor;

    public Preference(Context aCtx) {

        mySharedPreference = PreferenceManager
                .getDefaultSharedPreferences(aCtx);
        mySharedEditor = mySharedPreference.edit();

    }


    public void putLoginStatus(boolean aString) {
        mySharedEditor.putBoolean("String", aString);
        mySharedEditor.commit();
    }

    public boolean getLoginStatus() {
        return mySharedPreference.getBoolean("String", false);
    }

}