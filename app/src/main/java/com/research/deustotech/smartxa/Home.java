package com.research.deustotech.smartxa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void OnControlButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, HandControl.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }



    public void OnOpenHandButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, FullHandControl.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }
    public void ProfileButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, UserProfile.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }

    public void SettingsButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, Settings.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }
}
