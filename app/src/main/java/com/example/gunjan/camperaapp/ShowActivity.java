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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ShowActivity extends AppCompatActivity {

    ImageView imageView;
    TextView Hash, Encryp;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        imageView =  (ImageView) findViewById(R.id.image);
        Hash = (TextView) findViewById(R.id.hash);
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
                Bitmap bm=null;
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
                Hash.append(imageHelper.getHashcode().toString());
                Encryp.append(imageHelper.getEncrypted().toString());
            }
        }



    }

}



