package com.example.gunjan.camperaapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ShowActivity extends android.app.Fragment{

    ImageView imageView;
    TextView Hash, Encryp, Caption;
    DatabaseHelper databaseHelper;
    Button send, check, sel;
    Bitmap bm, bitmap;
    View myView;
    String path =  "";
    String imageid = "";


    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ((StartActivity) getActivity()).setActionBarTitle("Upload Photo");
        myView = inflater.inflate(R.layout.activity_show, container, false);
        imageView =  (ImageView) myView.findViewById(R.id.image);
        Hash = (TextView) myView.findViewById(R.id.hash);
        send = (Button) myView.findViewById(R.id.send);
        sel = (Button) myView.findViewById(R.id.select);
        check = (Button) myView.findViewById(R.id.check);
        Caption = (TextView) myView.findViewById(R.id.caption);
        Encryp = (TextView) myView.findViewById(R.id.encryp);
        databaseHelper =  new DatabaseHelper(getContext());
        handler=new Handler();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            Toast.makeText(getContext(), "permission not given", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "allow external read", Toast.LENGTH_SHORT).show();
        }


        sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"),1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if(bm!=null){
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                else{
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = Constants.url + "unhash/";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                       /* if(s.equals("true")){
                            Toast.makeText(getContext(), "Uploaded Successful", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Not same"+s, Toast.LENGTH_LONG).show();
                        }*/
                        Toast.makeText(getContext(), "ID received: "+s, Toast.LENGTH_LONG).show();
                        databaseHelper.insertId(path,s);
                        databaseHelper.changeUpld(path);
                        new CountDownTimer(30000,30000){
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getContext(), "Validate your submission", Toast.LENGTH_LONG);
                                check.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                    }
                }) {
                    //adding parameters to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("photo", imageString);
                        parameters.put("hashcode", Hash.getText().toString());
                        parameters.put("encrypted", Encryp.getText().toString());
                        parameters.put("caption", Caption.getText().toString());
                        return parameters;
                    }
                };
                queue.add(request);
                /*request.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 0;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 0;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        Toast.makeText(ShowActivity.this, "blabla", Toast.LENGTH_SHORT).show();
                    }
                });*/
                Toast.makeText(getContext(), "Sent to Server", Toast.LENGTH_SHORT).show();
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        check.setVisibility(View.VISIBLE);
                    }},3000);*/
                /*new CountDownTimer(120000, 120000){
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                            check.setVisibility(View.VISIBLE);
                    }
                }.start();*/
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select File"),70);

            }
        });




        return myView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    }

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);*//*
                imageView.setImageURI(photoURI);
                try {
                    hashed=hashImage(photoURI);
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                databaseHelper.insertImage(photoURI.toString(),hashed, decrypted.getText().toString());*/
                bm=null;
                bitmap = null;
                if (data.getData() != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                        imageView.setImageBitmap(bm);
                        path = data.toURI().toString();
                        Log.d("AAAAAAAAAAAAAAAA", data.toURI().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                imageView.setMaxWidth(200);
                imageView.setMaxHeight(200);

                //String path=RealPathUtil.getRealPathFromURI_API19(getContext(), data.getData());
                ImageHelper imageHelper = databaseHelper.getImage(path);
                Hash.setText(imageHelper.getHashcode().toString());
                Encryp.setText(imageHelper.getEncrypted().toString());
                Caption.setText(imageHelper.getCaption().toString());
            }
        }

        if(requestCode==70){
            imageid = data.toURI().toString();
            RequestQueue queue = Volley.newRequestQueue(getContext());

            String url = Constants.url + "check/";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    if(s.equals("matched"))
                        Toast.makeText(getContext(), "Your image has been correctly validated", Toast.LENGTH_SHORT).show();
                    else if(s.equals("not matched"))
                        Toast.makeText(getContext(), "Incorrect Location", Toast.LENGTH_SHORT).show();
                    else if(s.equals("warning"))
                        Toast.makeText(getContext(), "Your image has been tampered", Toast.LENGTH_SHORT).show();

                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getContext(), "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                }
            }) {
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("id", databaseHelper.getImage(imageid).getUuid());
                    Log.d("AAAAAAAAAAAAAAAAA",databaseHelper.getImage(imageid).getUuid() );
                    return parameters;
                }
            };
            queue.add(request);
        }



    }

    public void autoUpload(){
        /*final ImageHelper imageHelper = databaseHelper.nextUpld();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.url + "unhash/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                databaseHelper.changeUpld(imageHelper.getImageId());
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
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
        request.setTag("YAS");
        if(imageHelper.getImage()==null) queue.cancelAll("YAS");
        queue.add(request);*/


    }

}



