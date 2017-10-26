package com.research.deustotech.smartxa;

/**
 * Created by Kori on 10/25/17.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import android.bluetooth.*;

import java.util.Set;


public class BlueTooth {

    String address; //= null;
    //static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter bluetooth;
    private Set pairedDevices;

    public BlueTooth()
    {
         //= BluetoothAdapter.getDefaultAdapter();

    }


    public boolean isDeviceBlueToothCapable(BluetoothAdapter bluetooth)
    {
        if(bluetooth != null)
        {
            return true;
            // Continue with bluetooth setup.
        }
        else
        {
            return false;
        }
    }


    public void isBlueToothEnabled(BluetoothAdapter bluetooth)
    {

        String status;
        if (bluetooth.isEnabled()) {
            String mydeviceaddress = bluetooth.getAddress();
            String mydevicename = bluetooth.getName();
            int state = bluetooth.getState();
            status = mydevicename + " : " + mydeviceaddress + state;
        }
        else
        {
            status = "Bluetooth is not Enabled.";
        }

        //Toast.makeText(this, status, Toast.LENGTH_LONG) .show();
    }



}
