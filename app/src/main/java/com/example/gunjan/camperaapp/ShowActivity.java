package com.example.gunjan.camperaapp;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ShowActivity extends AppCompatActivity {

    ImageView imageView;
    TextView Hash, Encryp;
    DatabaseHelper databaseHelper;
    Button send;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        imageView =  (ImageView) findViewById(R.id.image);
        Hash = (TextView) findViewById(R.id.hash);
        send = (Button) findViewById(R.id.send);
        Encryp = (TextView) findViewById(R.id.encryp);
        databaseHelper =  new DatabaseHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            Toast.makeText(this, "permission not given", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "allow external read", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),1);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://172.16.75.172:8000/unhash/";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        if(s.equals("true")){
                            Toast.makeText(ShowActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(ShowActivity.this, "Not same"+s, Toast.LENGTH_LONG).show();
                        }
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ShowActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
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
                Toast.makeText(ShowActivity.this, "DONEEEEEE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
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
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        Log.d("AAAAAAAAAAAAAAAA", data.toURI().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imageView.setImageBitmap(bm);
                imageView.setMaxWidth(200);
                imageView.setMaxHeight(200);
                String path = data.toURI().toString();
                ImageHelper imageHelper = databaseHelper.getImage(path);
                Hash.setText(imageHelper.getHashcode().toString());
                Encryp.setText(imageHelper.getEncrypted().toString());
            }
        }



    }

}


