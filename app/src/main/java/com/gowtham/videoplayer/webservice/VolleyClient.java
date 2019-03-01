package com.gowtham.videoplayer.webservice;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gowtham.videoplayer.ApplicationClass;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class VolleyClient implements CommonValues {

    public static String TAG = VolleyClient.class.getSimpleName();
    private static Type myResponseClass;
    private static String URL;

    public static void execute(Context context, final int REQUEST_METHOD, String aURL,
                               final WebserviceCallback callback,
                               final HashMap<String, String> mParam, Type aResponse) {

        myResponseClass = aResponse;
        URL = aURL;

        Log.d("URL ---> ", URL);

        StringRequest stringRequest = new StringRequest(REQUEST_METHOD, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Success ---> ", response);

                Gson gson = new GsonBuilder().serializeNulls().create();
                callback.onSuccess(gson.fromJson(response, myResponseClass));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Failer ---> ", error.toString());

                callback.onFailer(SERVER_NOT_REACHABLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.d("Param ---> ", mParam.toString());
                return mParam;
            }

            @Override
            public String getUrl() {
                if (REQUEST_METHOD == Method.GET && !mParam.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder(URL);
                    int i = 1;
                    for (Map.Entry<String, String> entry : mParam.entrySet()) {
                        String key, value;
                        try {
                            key = URLEncoder.encode(entry.getKey(), "UTF-8");
                            value = URLEncoder.encode(entry.getValue(), "UTF-8");
                            if (!URL.contains(key.toLowerCase())) {
                                if (i == 1) {
                                    stringBuilder.append("?").append(key).append("=").append(value);
                                } else {
                                    stringBuilder.append("&").append(key).append("=").append(value);
                                }
                                URL = stringBuilder.toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                }
                Log.i("GET Method URL --> ", URL);
                return URL;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}
