package com.research.deustotech.smartxa;

/**
 * Created by Kori on 10/25/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import android.bluetooth.*;

import org.json.JSONObject;

import java.util.Set;


public class BlueTooth extends Activity {

    String address = null; //= MainActivity.smartxaAddress; //= null;
    //static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //BluetoothAdapter bluetooth;
    private Set pairedDevices;

    public static BluetoothSocket btSocket = null;
    public boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Context btoothContext;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public BlueTooth(String _address, Context _context)
    {
        this.address = _address;
        this.btoothContext = _context;
         //= BluetoothAdapter.getDefaultAdapter();

       // new ConnectBT().execute();

    }



    public void ConnectBlueTooth()
    {


//        btoothContext = MainActivity.mainActivityContext;
/*
        Handler mainHandler = new Handler(btoothContext.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                Toast.makeText(btoothContext, "Connecting to SmartXa. Please wait...", Toast.LENGTH_SHORT).show();
                //try{ Thread.sleep(4000); }catch(InterruptedException e){ }
            } // This is your code
        };

        mainHandler.post(myRunnable);

*/
//        Thread t = new Thread() {
//            public void run() {

                System.out.println("Starting new bluetooth thread");

                boolean ConnectSuccess = true;

                try {
                    //Toast.makeText(btoothContext, "Connecting to SmartXa. Please wait...", Toast.LENGTH_LONG).show();
                    System.out.println("In the try block");
                    if (btSocket == null || !isBtConnected) {
                        System.out.println("In the If statement");

                        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                        BluetoothDevice smartxaDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                        btSocket = smartxaDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection
                        isBtConnected = true;
                        System.out.println("Made it through the bluetooth setup " + isBtConnected);
                    }
                    else
                    {
                        System.out.println("unable to complete btooth setup. skipped the steps. Or bluetooth already connected");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to complete execution of bluetooth thread. " + e);
                    ConnectSuccess = false;//if the try failed, you can check the exception here
                }
                System.out.println("End of the thread process.");


                // troubleshoot
                //isBtConnected = true;

            }
//        };

//        t.start();

        //MainActivity.continueBtn.setClickable(true);

        /*
        try
        {

            t.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        */

//    }

    public static void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {
                //msg("Error");
            }
        }
        //finish(); //return to the first layout

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

/*
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
*/



    // No longer being used
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        /*
        static BluetoothSocket btSocket = null;
        private boolean isBtConnected = false;
        BluetoothAdapter myBluetooth;
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        private String address = MainActivity.smartxaAddress;
        private ProgressBar progress;
        */

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            //msg("starting connection");

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice smartxaDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = smartxaDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            //super.onPostExecute(result);

            if (!ConnectSuccess) {
                //msg("Connection Failed. Please verify that Smartxa is online and paired with your device.");
                //finish();
            } else {
                //msg("Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }

}
