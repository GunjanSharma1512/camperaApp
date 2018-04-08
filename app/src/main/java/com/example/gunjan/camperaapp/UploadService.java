package com.example.gunjan.camperaapp;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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
 * Created by Arti on 4/8/2018.
 */

public class UploadService extends Service {

    DatabaseHelper databaseHelper;
    public static final String RESULT = "result";
    private int result = Activity.RESULT_CANCELED;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Started upload", Toast.LENGTH_SHORT).show();
        databaseHelper =  new DatabaseHelper(getApplicationContext());
        final ImageHelper imageHelper = databaseHelper.nextUpld();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Constants.url + "unhash/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                result = Activity.RESULT_OK;
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("photo", Base64.encodeToString(imageHelper.getImage(), Base64.DEFAULT));
                Log.d("AAAAAAAAAAAAAAAAAAphoto", Base64.encodeToString(imageHelper.getImage(), Base64.DEFAULT));
                parameters.put("hashcode", imageHelper.getHashcode());
                parameters.put("encrypted", imageHelper.getEncrypted());
                parameters.put("caption", imageHelper.getCaption());
                return parameters;
            }
        };
        queue.add(request);
        Intent intent1 = new Intent("com.example.gunjan.camperaapp");
        intent.putExtra(RESULT, result);
        sendBroadcast(intent1);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
