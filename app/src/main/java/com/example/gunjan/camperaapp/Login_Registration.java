package com.example.gunjan.camperaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Akshama PC on 3/20/2018.
 */

public class Login_Registration extends AppCompatActivity{

    private Button login, registration;
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_registration);

        login = findViewById(R.id.button3);
        registration = findViewById(R.id.button4);
        username = findViewById(R.id.editText2);
        password = findViewById(R.id.editText);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("username") && password.getText().toString().equals("password")) {
                    Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Login_Registration.this, StartActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent in =new Intent(Login_Registration.this,Registration.class);
               startActivity(in);
            }
        });

    }
}
