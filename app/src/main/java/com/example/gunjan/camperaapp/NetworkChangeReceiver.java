package com.example.gunjan.camperaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arti on 3/31/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver{

    private static final String LOG_TAG = "NetworkChangeReceiver";
    private boolean isConnected = false;
    MainActivity main = null;
    DatabaseHelper databaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "Received notification about network status");
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            Log.v(LOG_TAG, "Now you are connected to Internet!");
                            Toast.makeText(context, "Internet available via Broadcast receiver", Toast.LENGTH_SHORT).show();
                            isConnected = true;
                            /*autoUpload(context);
                            Toast.makeText(context, "Upload function called", Toast.LENGTH_SHORT).show();*/
                            // do your processing here ---
                            // if you need to post any data to the server or get
                            // status
                            // update from the server
                        }
                        return true;
                    }
                }
            }
        }
        Log.v(LOG_TAG, "You are not connected to Internet!");
        Toast.makeText(context, "Internet NOT available via Broadcast receiver", Toast.LENGTH_SHORT).show();
        isConnected = false;
        return false;
    }




}