package com.example.gunjan.camperaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by dell15z on 27-Mar-18.
 */

public class UploadDetails extends Fragment {

    View myView;
    Button submit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ((StartActivity) getActivity()).setActionBarTitle("Upload Details ");
        myView = inflater.inflate(R.layout.upload_details, container, false);
        submit=(Button) myView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View myView) {

                //Code for sending details to db
            }
        }
        );
        return myView;
    }
}
