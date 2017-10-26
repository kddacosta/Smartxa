package com.research.deustotech.smartxa;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seekcircle.SeekCircle;

import android.bluetooth.*;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

//import static android.provider.Settings.Secure.ANDROID_ID;

public class FullHandControl extends AppCompatActivity {

    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter(); //null;
    private String address = MainActivity.smartxaAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_hand_control);

        msg("Move the slider to control your Smartxa device");
        new ConnectBTFullHand().execute();

        Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();


        SeekCircle seekCircle = (SeekCircle) findViewById(R.id.seekCircle);




        seekCircle.setOnSeekCircleChangeListener(new SeekCircle.OnSeekCircleChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekCircle seekCircle) {
            }

            @Override
            public void onStartTrackingTouch(SeekCircle seekCircle) {
            }

            @Override
            public void onProgressChanged(SeekCircle seekCircle, int progress, boolean fromUser) {

                updateAngle();
            }

        });

        updateAngle();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Disconnect();
    }

    private void updateAngle() {

        SeekCircle seekCircle = (SeekCircle) findViewById(R.id.seekCircle);
        TextView textProgress = (TextView) findViewById(R.id.textView4);

        if (textProgress != null && seekCircle != null) {
            try
            {
            int progress = seekCircle.getProgress();
            textProgress.setText(Integer.toString(progress) + "\u00b0");
            btSocket.getOutputStream().write(String.valueOf("6," + progress).getBytes());
            }
            catch (java.io.IOException e)
            {
                //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }

    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBTFullHand extends AsyncTask<Void, Void, Void>  // UI thread
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
            msg("starting connection");
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
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Please verify that Smartxa is online and paired with your device.");
                //finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }
}