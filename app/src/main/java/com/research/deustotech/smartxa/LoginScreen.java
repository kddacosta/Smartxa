package com.research.deustotech.smartxa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
    }


    public void loginButtonClicked(View v)
    {
        try
        {
            startActivity(new Intent(LoginScreen.this, Home.class));

            // TODO: POST request to get auth token from api
            // store token in var
            // Use token in app to send data to api

        }
        catch (Exception e)
        {
            // log error. failed to communicate with api: + e
        }
    }
}
