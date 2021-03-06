package com.example.gunjan.camperaapp;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gunjan.camperaapp.NetworkChangeReceiver;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.app.Activity.RESULT_OK;


public class MainActivity extends Fragment implements LocationListener {
    private Button takePictureButton;
    private ImageView imageView;
    private boolean isConnected = false;
    Bitmap bm;
    String idef = "";
    Uri file;
    private static TextView box;
    String mCurrentPhotoPath;
    File photoFile;
    Uri photoURI;
    Button  ok;
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
    TextInputEditText caption;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        myView=inflater.inflate(R.layout.activity_main,container,false);
        takePictureButton = (Button) myView.findViewById(R.id.button_image);
        imageView = (ImageView) myView.findViewById(R.id.imageview);
        hashid = (TextView) myView.findViewById(R.id.hashid);
        decrypted = (TextView) myView.findViewById(R.id.decrypted);
        ok = (Button) myView.findViewById(R.id.okay);
        box = (TextView)  myView.findViewById(R.id.box);
        databaseHelper = new DatabaseHelper(getActivity());
        caption = (TextInputEditText) myView.findViewById(R.id.caption);
        context = getContext();

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
                /*latitudePosition.setText(String.valueOf(location.getLatitude()));
                longitudePosition.setText(String.valueOf(location.getLongitude()));
                Log.d("AAAAAAAAAAAAAA", String.valueOf(location.getLatitude()));
                Log.d("AAAAAAAAAAAAAA", String.valueOf(location.getLongitude()));*/
                latitudePosition.setText("28.62");
                longitudePosition.setText("77.22");
                ///////////////////

                Calendar calendar = Calendar.getInstance();
                // calendar.setTime(yourdate);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH)+1;
                int year = calendar.get(Calendar.YEAR);

                currentCity.setText("TIME: " + hours + ":" + minute + " DATE: " + day + "/" + month + "/" + year);
                geotaggedData="28.62"+"_"+"77.22"+"_"+String.valueOf(hours)+"_"+String.valueOf(minute)+"_"+String.valueOf(day)+"_"+String.valueOf(month)+"_"+String.valueOf(year)+"_";
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
                //getAddressFromLocation(location, getContext(), new GeoCoderHandler());
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



        BroadcastReceiver receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                if (bundle != null) {
                    int resultCode = bundle.getInt(UploadService.RESULT);
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(getContext(),
                                "Upload successful",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };


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
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
               bm=null;
               if (data != null) {
                   try {
                       bm = MediaStore.Images.Media.getBitmap( getContext().getContentResolver(), data.getData());
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               imageView.setImageBitmap(bm);
               final ByteArrayOutputStream stream = new ByteArrayOutputStream();
               bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
               caption.setVisibility(View.VISIBLE);
               ok.setVisibility(View.VISIBLE);
               Toast.makeText(getContext(), "ENTER CAPTION!", Toast.LENGTH_SHORT).show();

               final Bitmap finalBm = bm;
               ok.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       try {
                           hashed=hashImage(finalBm);
                           Log.d("AAAAAAAAAAAAAAAA", hashed);
                           idef = data.toURI().toString();
                           ByteArrayOutputStream baos = new ByteArrayOutputStream();
                           if(finalBm!=null){
                               finalBm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                           }

                           byte[] imageBytes = baos.toByteArray();
                           final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                           databaseHelper.insertImage(data.toURI().toString(),hashed, decrypted.getText().toString(), caption.getText().toString(), stream.toByteArray());
                           autoUpload(data.toURI().toString(), imageString, hashed, decrypted.getText().toString(), caption.getText().toString());
                           Log.d("ARRRRRRRAAAYY", stream.toByteArray().toString());
                           Toast.makeText(getContext(), "PHOTO SAVED", Toast.LENGTH_SHORT).show();
                           getFragmentManager().beginTransaction().replace(R.id.content_frame,new MainActivity()).commit();
                       } catch (NoSuchAlgorithmException e) {
                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });




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
        //box.setText(mCurrentPhotoPath);
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
        MessageDigest md = MessageDigest.getInstance("SHA-256");
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

    public void autoUpload(final String iden, final String ide, final String h,final String e,final String c){

        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            Log.v("YAAAAAAS", "Now you are connected to Internet!");
                            Toast.makeText(getContext(), "Internet available via Broadcast receiver", Toast.LENGTH_SHORT).show();
                            isConnected = true;

                            RequestQueue queue = Volley.newRequestQueue(getContext());
                            String url = Constants.url + "unhash/";
                            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    Toast.makeText(context, "ID received: "+s, Toast.LENGTH_LONG).show();
                                    databaseHelper.insertId(iden,s);
                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                   Log.d("ERRRRRROR", String.valueOf(volleyError));
                                    Toast.makeText(context, "Upload error -> "+volleyError, Toast.LENGTH_LONG).show();
                                }
                            }) {
                                //adding parameters to send
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parameters = new HashMap<String, String>();
                                    parameters.put("photo", ide);
                                    parameters.put("hashcode", h);
                                    parameters.put("encrypted", e);
                                    parameters.put("caption", c);
                                    Log.d("YAAAAS", ide);
                                    Log.d("YAAAAS", h);
                                    Log.d("YAAAAS", e);
                                    Log.d("YAAAAS", c);
                                    return parameters;
                                }
                            };

                            queue.add(request);
                            Toast.makeText(getContext(), "Upload started", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }


    }





}