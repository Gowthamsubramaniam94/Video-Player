package com.gowtham.videoplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    public boolean isInternetOn(Context aContext) {
        //
        boolean aResult = false;
        try {
            //
            ConnectivityManager aConnecMan = (ConnectivityManager) aContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            //
            if ((aConnecMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
                    || (aConnecMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING)
                    || (aConnecMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING)
                    || (aConnecMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)) {
                aResult = true;

            } else if ((aConnecMan.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED)
                    || (aConnecMan.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)) {

                aResult = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aResult;
    }
}
