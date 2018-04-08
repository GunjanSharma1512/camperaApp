package com.example.gunjan.camperaapp;

import android.graphics.Bitmap;

/**
 * Created by Arti on 3/24/2018.
 */

public class ImageHelper {

    private String imageId;
    private String hashcode;
    private String encrypted;
    private String caption;
    private String uuid;
    private String upld;
    private byte[] image;


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUpld() {

        return upld;
    }

    public void setUpld(String upld) {
        this.upld = upld;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

}
