package com.gowtham.videoplayer.webservice;

import android.content.Context;

import com.android.volley.Request;

import java.util.HashMap;


public class Webservice implements CommonValues {

    private String TAG = Webservice.class.getSimpleName();
    private static String mURL = "";


    public void getVideoDetails(Context context, final WebserviceCallback callback) {
        try {
            mURL = "https://interview-e18de.firebaseio.com/media.json";

            HashMap<String, String> mParam = new HashMap<>();
            mParam.put("print", "pretty");

            VolleyClient.execute(context, Request.Method.GET, mURL, callback, mParam, ReturnValues.VideoDetails[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
