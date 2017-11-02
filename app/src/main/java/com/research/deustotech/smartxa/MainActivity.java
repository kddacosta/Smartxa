package com.research.deustotech.smartxa;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter(); //null;
    private Set pairedDevices;
    public static ArrayList list = new ArrayList();
    public static String smarxaDeviceName;
    public static String smartxaAddress;
    public static Dictionary<String,String> smartxaDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void continueButtonClicked(View v)
    {
        //startActivity(new Intent(MainActivity.this, LoginScreen.class));

        //BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        //TextView textview = (TextView)findViewById(R.id.textView);

        //BlueTooth smartxaBluetooth = new BlueTooth();
        //smartxaBluetooth.bluetooth = BluetoothAdapter.getDefaultAdapter();

        startActivity(new Intent(MainActivity.this, LoginScreen.class));

        if(bluetooth != null)
        {

            // Continue with bluetooth setup.
            //Toast.makeText(this, "Device is bluetooth capable", Toast.LENGTH_LONG).show();

            String status;
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                int state = bluetooth.getState();

                status = mydevicename + " : " + mydeviceaddress + " : " + state + " : ";

                Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                createPairedDevicesList();
                startActivity(new Intent(MainActivity.this, LoginScreen.class));
            }
            else
            {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                status = "BlueTooth is disabled. Smartxa is enabling bluetooth on your device.";
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                startActivityForResult(turnBTon,1);

                //bluetooth.enable();

                //startActivity(new Intent(MainActivity.this, LoginScreen.class));
            }



            //bluetooth.listenUsingRfcommWithServiceRecord("raspberrypi", new UUID());


        }
        else
        {
            Toast.makeText(this, "Device is not bluetooth capable", Toast.LENGTH_LONG).show();

        }

    }



    private void createPairedDevicesList()
    {
        pairedDevices = bluetooth.getBondedDevices();
        // ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for (BluetoothDevice bt: bluetooth.getBondedDevices())
            {
               // list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                //list.add(bt.getAddress());
                //smartxaDevice.put(bt.getName(),bt.getAddress());
                smarxaDeviceName = bt.getName();
                smartxaAddress = bt.getAddress();

                Toast.makeText(getApplicationContext(),"Paired Device: " + bt.getName() + " " + bt.getAddress(), Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
       // devicelist.setAdapter(adapter);
        //devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

}


