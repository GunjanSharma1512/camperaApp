package com.example.gunjan.camperaapp;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    Button submit;
    DatabaseHelper databaseHelper;
    TextInputEditText agency, add, contact, email, pass, cpass;
    TextInputLayout Pass, CPass;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        submit = (Button) findViewById(R.id.submit);
        agency = (TextInputEditText) findViewById(R.id.agency);
        add = (TextInputEditText) findViewById(R.id.add);
        contact = (TextInputEditText) findViewById(R.id.contact);
        email = (TextInputEditText) findViewById(R.id.email);
        pass = (TextInputEditText) findViewById(R.id.pass);
        cpass = (TextInputEditText) findViewById(R.id.cpass);
        Pass = (TextInputLayout) findViewById(R.id.textInputLayout1);
        CPass = (TextInputLayout) findViewById(R.id.textInputLayout);
//        databaseHelper =  new DatabaseHelper(Registration.this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getText().toString().equals(cpass.getText().toString())) {
                    RequestQueue queue = Volley.newRequestQueue(Registration.this);
                    String url = Constants.url+ "register/";
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                       /* if(s.equals("true")){
                            Toast.makeText(getContext(), "Uploaded Successful", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Not same"+s, Toast.LENGTH_LONG).show();
                        }*/
                       agency.setText("");
                       add.setText("");
                       contact.setText("");
                       email.setText("");
                       pass.setText("");
                       cpass.setText("");
                       onBackPressed();
                            Toast.makeText(Registration.this, "Registration Successful. Your submission ID is: " + s, Toast.LENGTH_LONG).show();

//                        databaseHelper.insertId(path, s);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(Registration.this,  volleyError.toString(), Toast.LENGTH_LONG).show();
                            ;
                        }
                    }) {
                        //adding parameters to send
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("agency", agency.getText().toString());
                            parameters.put("add", add.getText().toString());
                            parameters.put("contact", contact.getText().toString());
                            parameters.put("email", email.getText().toString());
                            parameters.put("pass", pass.getText().toString());
                            return parameters;
                        }
                    };
                    queue.add(request);


                } else {
                    Toast.makeText(Registration.this, "Passwords do NOT match", Toast.LENGTH_LONG).show();
                    Pass.setErrorEnabled(true);
                    Pass.setError("Incorrect");
                    CPass.setCounterEnabled(true);
                    CPass.setError("Incorrect");
                }

            }


        });
    }}