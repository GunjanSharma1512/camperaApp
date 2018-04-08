package com.example.gunjan.camperaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Arti on 3/24/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private final String TAG = "DatabaseHelperClass";
    private static final int databaseVersion = 5;
    private static final String databaseName = "dbTest";
    private static final String TABLE_IMAGE = "ImageTable";

    // Image Table Columns names
    private static final String COL_ID = "col_id";
    private static final String IMAGE_ID = "image_id";
   // private static final String IMAGE_BITMAP = "image_bitmap";
    private static final String HASH_ID = "hash_id";
    private static final String ENCRYPTED =  "encrypted";
    private static final String UUID = "uuid";
    private static final String CAPTION = "caption";
    private static final String UPLD = "uploaded";
    private static final String BMP = "bitmap";

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + COL_ID + " INTEGER PRIMARY KEY ,"
                + IMAGE_ID + " TEXT,"
                + HASH_ID + " TEXT,"
                + UUID + " TEXT,"
                + CAPTION + " TEXT,"
                + UPLD + " TEXT,"
                + BMP + " BLOB,"
                + ENCRYPTED + " TEXT )";
               // + IMAGE_BITMAP + " BLOB )";
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(sqLiteDatabase);
    }

    public void insertImage(String imageId, String hash, String encrypted, String caption, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        values.put(HASH_ID, hash);
        values.put(ENCRYPTED, encrypted);
        values.put(CAPTION, caption);
        values.put(UPLD, "false");
        values.put(BMP, image);
        /*Bitmap bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(IMAGE_BITMAP, stream.toByteArray());*/
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public ImageHelper getImage(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor2 = db.query(TABLE_IMAGE,
                new String[] {COL_ID, IMAGE_ID, HASH_ID, ENCRYPTED, CAPTION, UUID, UPLD},IMAGE_ID
                        +" LIKE '"+imageId+"%'", null, null, null, null);
        ImageHelper imageHelper = new ImageHelper();

        if (cursor2.moveToFirst()) {
            do {
                imageHelper.setImageId(cursor2.getString(1));
                //imageHelper.setImageByteArray(cursor2.getBlob(2));
                imageHelper.setHashcode(cursor2.getString(2));
                imageHelper.setEncrypted(cursor2.getString(3));
                imageHelper.setCaption(cursor2.getString(4));
                imageHelper.setUuid(cursor2.getString(5));
                imageHelper.setUpld(cursor2.getString(6));
            } while (cursor2.moveToNext());
        }

        cursor2.close();
        db.close();
        return imageHelper;
    }

    public void insertId(String imageId, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        values.put(UUID, id);
        String selection = IMAGE_ID + "=?" ;
        db.update(TABLE_IMAGE, values, selection, new String[]{imageId} );
        db.close();
    }

    public void changeUpld(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        values.put(UPLD, "true");
        String selection = IMAGE_ID + "=?" ;
        db.update(TABLE_IMAGE, values, selection, new String[]{imageId} );
        db.close();
    }

    public ImageHelper nextUpld() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor2 = db.query(TABLE_IMAGE,
                new String[] { IMAGE_ID, HASH_ID, ENCRYPTED, CAPTION,  BMP},null, null, null, null, null);
        Log.d("ROOOOOOOWS", String.valueOf(cursor2.getCount()));
        ImageHelper imageHelper = new ImageHelper();
        if(cursor2.moveToFirst()){
            do{
                Log.d("AAAAAAAA", String.valueOf(cursor2.getPosition()));
                Log.d("AAAAAAAA", String.valueOf(cursor2.getColumnCount()));
                imageHelper.setImageId(cursor2.getString(0));
                //imageHelper.setImageByteArray(cursor2.getBlob(2));
                imageHelper.setHashcode(cursor2.getString(1));
                imageHelper.setEncrypted(cursor2.getString(2));
                imageHelper.setCaption(cursor2.getString(3));
                imageHelper.setImage(cursor2.getBlob(4));
            }while(cursor2.moveToNext());

        }
        cursor2.close();
        db.close();
        return imageHelper;

      /*  if (cursor2.getCount()>=1) {
            if(cursor2.moveToPosition(1)){

                do{
                    Log.d("AAAAAAAA", cursor2.getString(0));
                    Log.d("AAAAAAAA", cursor2.getString(1));
                    Log.d("AAAAAAAA", cursor2.getString(2));
                    Log.d("AAAAAAAA", cursor2.getString(3));
                    Log.d("AAAAAAAA", cursor2.getBlob(4).toString());
                    imageHelper.setImageId(cursor2.getString(0));
                    //imageHelper.setImageByteArray(cursor2.getBlob(2));
                    imageHelper.setHashcode(cursor2.getString(1));
                    imageHelper.setEncrypted(cursor2.getString(2));
                    imageHelper.setCaption(cursor2.getString(3));
                    imageHelper.setImage(cursor2.getBlob(4));
                    cursor2.close();
                    db.close();
                    return imageHelper;

                }while(cursor2.moveToNext());
            }


        }
        cursor2.close();
        db.close();
        return imageHelper;*/

    }


    public int getProfilesCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.query(TABLE_IMAGE,
                new String[] {COL_ID, IMAGE_ID, HASH_ID, ENCRYPTED, CAPTION, UUID, UPLD, BMP},UPLD
                        +" LIKE '"+"false"+"%'", null, null, null, null);

        int count = cursor2.getCount();
        cursor2.close();
        return count;
    }



}
