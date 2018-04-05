package com.example.gunjan.camperaapp;

/**
 * Created by Arti on 3/31/2018.
 */

public class DataModel {

    String name;
    String version;

    public DataModel(String name, String version ) {
        this.name = name;
        this.version = version;

    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }


}