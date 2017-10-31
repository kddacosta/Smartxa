package com.research.deustotech.smartxa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.*;

import com.seekcircle.SeekCircle;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HandControl extends AppCompatActivity {


    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;
    private ProgressBar progress;

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter(); //null;
    private String address = MainActivity.smartxaAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //List<Integer> motorsList = null;

    JSONObject jsonMotors = new JSONObject();

    ImageButton f1, f2, f3, f4, f5;
    TextView a, b, c, d, e, Seekbar_Text;
    SeekBar seek_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_control);

        try
        {
            jsonMotors.put("motor1", 0);
            jsonMotors.put("motor2", 0);
            jsonMotors.put("motor3", 0);
            jsonMotors.put("motor4", 0);
            jsonMotors.put("motor5", 0);
            //jsonMotors.put("motor6", 0);
            jsonMotors.put("angle", 0);

        }
        catch (Exception e)
        {

        }

        new ConnectBTFinger().execute();

        f1 = (ImageButton) findViewById(R.id.finger1);
        f2 = (ImageButton) findViewById(R.id.finger2);
        f3 = (ImageButton) findViewById(R.id.finger3);
        f4 = (ImageButton) findViewById(R.id.finger4);
        f5 = (ImageButton) findViewById(R.id.finger5);

        f1.setImageAlpha(0);
        f2.setImageAlpha(0);
        f3.setImageAlpha(0);
        f4.setImageAlpha(0);
        f5.setImageAlpha(0);

        //We dont need this..
       /* a = (TextView) findViewById(R.id.f1Text);
        b = (TextView) findViewById(R.id.f2Text);
        c = (TextView) findViewById(R.id.f3Text);
        d = (TextView) findViewById(R.id.f4Text);
        e = (TextView) findViewById(R.id.f5Text); */

        seekbar();
    }


    public void seekbar(/*final TextView visibility*/){
        seek_bar = (SeekBar)findViewById(R.id.seekBar);
        Seekbar_Text = (TextView)findViewById(R.id.SeekbarText);
        Seekbar_Text.setText(seek_bar.getProgress() + "/" + seek_bar.getMax());
        int progress_value;

        seek_bar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        Seekbar_Text.setText(progress + "/" + seek_bar.getMax());
                        //visibility.setAlpha(200 - 2*progress_value);

                        updateAngle();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Seekbar_Text.setText(progress_value + "/" + seek_bar.getMax());

                    }
                }
        );

    }

    public void f1Clicked(View V)
    {
        if(f1.getImageAlpha() == 0){
            f1.setImageAlpha(255);
            //motorsList.add(1);
            try {
                jsonMotors.remove("motor1");
                jsonMotors.put("motor1", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f1.setImageAlpha(0);
            //motorsList.remove(1);
            try {
                jsonMotors.remove("motor1");
                jsonMotors.put("motor1", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void f2Clicked(View V)
    {
        if(f2.getImageAlpha() == 0){
            f2.setImageAlpha(255);
            //motorsList.add(2);
            try {
                jsonMotors.remove("motor2");
                jsonMotors.put("motor2", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f2.setImageAlpha(0);
            //motorsList.remove(2);
            try {
                jsonMotors.remove("motor2");
                jsonMotors.put("motor2", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void f3Clicked(View V)
    {
        if(f3.getImageAlpha() == 0){
            f3.setImageAlpha(255);
            //motorsList.add(3);
            try {
                jsonMotors.remove("motor3");
                jsonMotors.put("motor3", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f3.setImageAlpha(0);
            //motorsList.remove(3);
            try {
                jsonMotors.remove("motor3");
                jsonMotors.put("motor3", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void f4Clicked(View V)
    {
        if(f4.getImageAlpha() == 0){
            f4.setImageAlpha(255);
            //motorsList.add(4);
            try {
                jsonMotors.remove("motor4");
                jsonMotors.put("motor4", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f4.setImageAlpha(0);
            //motorsList.remove(4);
            try {
                jsonMotors.remove("motor4");
                jsonMotors.put("motor4", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void f5Clicked(View V)
    {
        if(f5.getImageAlpha() == 0){
            f5.setImageAlpha(255);
            //motorsList.add(5);
            try {
                jsonMotors.remove("motor5");
                jsonMotors.put("motor5", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f5.setImageAlpha(0);
            //motorsList.remove(5);
            try {
                jsonMotors.remove("motor5");
                jsonMotors.put("motor5", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Disconnect();
    }



    public void setTextBox(TextView finger)
    {
        if(finger.getVisibility() == View.INVISIBLE) {
            finger.setVisibility(View.VISIBLE);

        } else if (finger.getVisibility() == View.VISIBLE){
            finger.setVisibility(View.INVISIBLE);
        }
    }


    private void updateAngle() {

        /*
        String motors = "";
        for ( Object m : motorsList)
        {
             motors = motors.concat(m + ",");
        }
        */
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        //TextView textProgress = (TextView) findViewById(R.id.textView4);

        if (/*textProgress != null && */seekBar != null) {
            try
            {
            int progress = seekBar.getProgress();
            //textProgress.setText(Integer.toString(progress) + "\u00b0");
                try {
                    jsonMotors.remove("angle");
                    jsonMotors.put("angle", progress);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                btSocket.getOutputStream().write(String.valueOf(jsonMotors.toString()).getBytes());
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


    private class ConnectBTFinger extends AsyncTask<Void, Void, Void>  // UI thread
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
            //progress.setVisibility(View.VISIBLE); //.show(this, "Connecting...", "Please wait!!!");  //show a progress dialog
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
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }


}
