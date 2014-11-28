package com.maadlabs.twitterui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by brainfreak on 9/4/14.
 */
public class ConnectionManager extends BroadcastReceiver{

    public static final String NETWORK_CONNECTED = "Network Connected";
    public static final String NETWORK_DISCONNECTED = "Network Disconnected";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        return false;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //alertNetworkStatus(context);
    }


}
