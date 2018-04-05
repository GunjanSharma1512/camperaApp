package com.example.gunjan.camperaapp;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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

/**
 * Created by dell15z on 27-Mar-18.
 */

public class UploadPhoto extends Fragment {

    View myView;
    ImageView imageView;
    TextView Hash, Encryp;
    DatabaseHelper databaseHelper;
    Button upload, sel;
    Bitmap bm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        myView = inflater.inflate(R.layout.activity_show, container, false);

        imageView =  (ImageView)myView.findViewById(R.id.image);
        Hash = (TextView) myView.findViewById(R.id.hash);
        upload = (Button) myView.findViewById(R.id.send);
        sel = (Button) myView.findViewById(R.id.select);
        Encryp = (TextView) myView.findViewById(R.id.encryp);
        databaseHelper =  new DatabaseHelper(getContext());

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


        upload.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View myView) {

                                          ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                          bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                          byte[] imageBytes = baos.toByteArray();
                                          final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                          RequestQueue queue = Volley.newRequestQueue(getContext());
                                          String url ="http://172.16.75.172:8000/unhash/";
                                          StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                                              @Override
                                              public void onResponse(String s) {
                                                  if(s.equals("true")){
                                                      Toast.makeText(getContext(), "Uploaded Successful", Toast.LENGTH_LONG).show();
                                                  }
                                                  else{
                                                      Toast.makeText(getContext(), "Not same"+s, Toast.LENGTH_LONG).show();
                                                  }
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
                                                  return parameters;
                                              }
                                          };
                                          queue.add(request);

                                          Toast.makeText(getContext(), "Sent to Server", Toast.LENGTH_SHORT).show();
                                      }
                                  }
        );
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
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                databaseHelper.insertImage(photoURI.toString(),hashed, decrypted.getText().toString());*/
                bm=null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                       // Log.d("AAAAAAAAAAAAAAAA", data.toURI().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imageView.setImageBitmap(bm);
                imageView.setMaxWidth(200);
                imageView.setMaxHeight(200);
                // String path = data.toURI().toString();
                String path=RealPathUtil.getRealPathFromURI_API19(getContext(), data.getData());
                ImageHelper imageHelper = databaseHelper.getImage(path);
                Hash.setText(imageHelper.getHashcode().toString());
                Encryp.setText(imageHelper.getEncrypted().toString());
            }
        }



    }

}
