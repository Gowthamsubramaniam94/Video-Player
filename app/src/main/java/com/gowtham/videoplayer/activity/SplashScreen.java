package com.gowtham.videoplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.gowtham.videoplayer.R;
import com.gowtham.videoplayer.sharedpreference.Preference;
import com.gowtham.videoplayer.webservice.CommonValues;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class SplashScreen extends FragmentActivity implements CommonValues {

    private String TAG = SplashScreen.class.getSimpleName();
    private FragmentActivity mContext;
    private Preference mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        mContext = SplashScreen.this;
        mPref = new Preference(mContext);

        Handler aHoldScreen = new Handler();
        aHoldScreen.postDelayed(new Runnable() {
            public void run() {
                callMainScreen();
            }
        }, DELAY_TIME_FOR_SPLASH_SCREEN);

    }


    private void callMainScreen() {

        Intent aIntent = null;
        if (!mPref.getLoginStatus()) {
            aIntent = new Intent(getApplicationContext(),
                    LoginActivity.class);
        } else {
            aIntent = new Intent(getApplicationContext(),
                    VideoListActivity.class);
        }

        aIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(aIntent);

        // ---Close current screen---
        finish();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}