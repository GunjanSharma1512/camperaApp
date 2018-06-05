package com.example.gunjan.camperaapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
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
 * Created by Akshama PC on 2/06/2018.
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
                            Notification(context,"Internet available, Click HERE to upload your images");
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
        Notification(context,"Internet NOT available");
        return false;
    }

    public void Notification(Context context, String message) {
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher) //icon
                .setContentTitle("DAVPConnect") //tittle
                .setAutoCancel(true)//swipe for delete
                .setContentText(message); //content
        Intent notificationIntent = new Intent(context,Login_Registration.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build()
        );
    }


}
