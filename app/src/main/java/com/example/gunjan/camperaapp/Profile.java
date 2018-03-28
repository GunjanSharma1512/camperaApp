package com.example.gunjan.camperaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dell15z on 27-Mar-18.
 */

public class Profile extends Fragment {

    View myView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        myView = inflater.inflate(R.layout.activity_profile, container, false);
        ((StartActivity) getActivity()).setActionBarTitle("Profile");
        return myView;

    }


}
