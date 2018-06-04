package com.example.dell15z.cardlayout;

import android.net.Uri;

/**
 * Created by dell15z on 03-Jun-18.
 */

public class DataObject {

       private Uri uri;

    DataObject (Uri uri){
        this.uri= uri;
    }

    public Uri getUri() {
        return uri;
    }

}