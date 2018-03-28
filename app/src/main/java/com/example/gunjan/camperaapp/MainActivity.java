package com.example.gunjan.camperaapp;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.*;
import android.view.View;
import android.net.Uri;
import android.widget.Button;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.app.Activity.RESULT_OK;


public class MainActivity extends Fragment implements LocationListener {
    private Button takePictureButton;
    private ImageView imageView;
    Uri file;
    private static TextView box;
    String mCurrentPhotoPath;
    File photoFile;
    Uri photoURI;
    Button upload;
    private TextView latitudePosition;
    private TextView longitudePosition;
    private TextView currentCity,hashid,decrypted;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MainActivity";
    private String hashed,geotaggedData,encryptedData;
    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    Cipher cipher, cipher1;
    View myView;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        myView=inflater.inflate(R.layout.activity_main,container,false);
        takePictureButton = (Button) myView.findViewById(R.id.button_image);
        imageView = (ImageView) myView.findViewById(R.id.imageview);
        hashid = (TextView) myView.findViewById(R.id.hashid);
        decrypted = (TextView) myView.findViewById(R.id.decrypted);
        upload = (Button) myView.findViewById(R.id.upload);
        box = (TextView)  myView.findViewById(R.id.box);
        databaseHelper = new DatabaseHelper(getActivity());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        latitudePosition = (TextView) myView.findViewById(R.id.latitude);
        longitudePosition = (TextView) myView.findViewById(R.id.longitude);
        currentCity = (TextView) myView.findViewById(R.id.city);

        locationManager = (LocationManager) getContext().getSystemService(Service.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            Toast.makeText(getContext(), "permission not given", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, MainActivity.this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                latitudePosition.setText(String.valueOf(location.getLatitude()));
                longitudePosition.setText(String.valueOf(location.getLongitude()));
                Log.d("AAAAAAAAAAAAAA", String.valueOf(location.getLatitude()));
                Log.d("AAAAAAAAAAAAAA", String.valueOf(location.getLongitude()));
                ///////////////////

                Calendar calendar = Calendar.getInstance();
                // calendar.setTime(yourdate);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                currentCity.setText("TIME: " + hours + ":" + minute + " DATE: " + day + "/" + month + "/" + year);
                geotaggedData=String.valueOf(location.getLatitude())+"_"+String.valueOf(location.getLongitude())+"_"+String.valueOf(hours)+"_"+String.valueOf(minute)+"_"+String.valueOf(day)+"_"+String.valueOf(month)+"_"+String.valueOf(year)+"_";
                try {
                    encryptedData=encrypt(geotaggedData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    decrypted.setText(decrypt(encryptedData));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
                ///////////////////////
                //TODO - WHY THIS?
                getAddressFromLocation(location, getContext(), new GeoCoderHandler());
            }
        } else {
            // showGPSDisabledAlertToUser();
            Toast.makeText(getContext(), "provider is disabled", Toast.LENGTH_SHORT).show();

        }

        //Take Picture button functionality
        takePictureButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View myView)
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getContext(),
                                "com.example.gunjan.camperaapp.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), ShowActivity.class));
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ShowActivity()).commit();
            }
        });



     return myView;
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getContext(), "provider is disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getCountryName();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            // currentCity.setText(result);
       /*     Calendar calendar = Calendar.getInstance();
            // calendar.setTime(yourdate);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            currentCity.setText("TIME: " + hours + " DATE: " + day + "/" + month + "/" + year);
*/

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        } else if (requestCode==99){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
                }

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.

            }
            return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (requestCode == 1) {
         //   if (resultCode == RESULT_OK) {
               /* Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");*/
                /*imageView.setImageBitmap(imageBitmap);*/
                //imageView.setImageURI(photoURI);
           //     Intent intent = new Intent();
             //   intent.setType("image/*");
               // intent.setAction(Intent.ACTION_GET_CONTENT);
               // startActivityForResult(Intent.createChooser(intent, "Select File"),2);

          //  }
        //}

       if(requestCode==1){
            if (resultCode == RESULT_OK) {
                /*Bitmap bm=null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // imageView.setImageBitmap(bm);
                imageView.setImageURI(photoURI);
                try {
                    hashed=hashImage(bm);
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              //  Log.d("AAAAAAAAAAAAAAAAAAAAA", data.toURI().toString());
                Log.d("AAAAAAAAAAAAAAAAAAAAA", hashed);
                Log.d("AAAAAAAAAAAAAAAAAAAAA", decrypted.getText().toString());
                databaseHelper.insertImage(mCurrentPhotoPath,hashed, decrypted.getText().toString());*/

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"),2);

            }
        }

       else if(requestCode==2){
           if (resultCode == RESULT_OK) {
               Bitmap bm=null;
               if (data != null) {
                   try {
                       bm = MediaStore.Images.Media.getBitmap( getContext().getContentResolver(), data.getData());
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               imageView.setImageBitmap(bm);
               try {
                   hashed=hashImage(bm);
               } catch (NoSuchAlgorithmException e) {
                   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
               }
               Log.d("AAAAAAAAAAAAAAAAAAAAA", data.toURI().toString());
               Log.d("AAAAAAAAAAAAAAAAAAAAA", hashed);
               Log.d("AAAAAAAAAAAAAAAAAAAAA", decrypted.getText().toString());
               databaseHelper.insertImage(data.toURI().toString(),hashed, decrypted.getText().toString(), "Some Caption here");


           }
       }



    }

    /*private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        box.setText(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpg");
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        box.setText(mCurrentPhotoPath);
        return image;
    }


    private String hashImage(Bitmap bm) throws NoSuchAlgorithmException {

        byte[] bitmapdata;

        try {
            Bitmap bitmap = bm;

           // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoFile);
/*
            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            bitmapdata = byteBuffer.array();*/

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmapdata = stream.toByteArray();
        } catch (Exception e) {
            bitmapdata = "hey".getBytes();
        }
        System.out.println("Start MD5 Digest");
        MessageDigest md = MessageDigest.getInstance("MD5");
       // md.update(bitmapdata);
      //  byte[] hash = md.digest();
      //  hashid.setText(bytesToString(hash));
      //  return bytesToString(hash);

        ///////////////////////

        byte[] hash = md.digest(bitmapdata);
        String encoded = Base64.encodeToString(hash,Base64.DEFAULT);
        // System.out.println(Arrays.toString(thedigest));
        hashid.setText(encoded);
      //  System.out.println(encoded);
        return encoded;
        //////////////////////

    }

    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public String encrypt (String plain) throws Exception {
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        Log.d(TAG,publicKey.getFormat()+" "+publicKey.toString());
        privateKey = kp.getPrivate();

        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte [] encryptedBytes;
        encryptedBytes = cipher.doFinal(plain.getBytes());

        String encrypted = bytesToString(encryptedBytes);
        return encrypted;
    }


    public String decrypt (String result) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] decryptedBytes;
        String decrypted;
        cipher1=Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, publicKey);
        decryptedBytes = cipher1.doFinal(stringToBytes(result));
        decrypted = new String(decryptedBytes);
        //i2.setImageResource(R.drawable.smiley.png);

        return decrypted;

    }
    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

}