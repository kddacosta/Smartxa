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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HandControl extends AppCompatActivity {


    // TODO: Add back and forward buttons to activities
    // TODO: Add User settings
    // TODO: API is not responding to the get request
    // TODO: A trial == open and close hand one time
    // Reset how the trials are tracked. Send how many trials were run, and the max value
    // Interval for how many repitions: 10% threshold
    // TODO: Add labels, images, and icons
    // TODO: Add repetition for the test
    // TODO: Add settings to the test mode
    // Repetitions: 5 10 15 20
    // Max angle: 25  30 40 50 75
    // how long to hold position



    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;
    private ProgressBar progress;

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    private String address = MainActivity.smartxaAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    JSONObject jsonMotors = new JSONObject();

    ImageButton f1, f2, f3, f4, f5;
    TextView a, b, c, d, e, Seekbar_Text;
    SeekBar seek_bar;
    String Token;

    BlueTooth _bluetooth_ctrl;

    public int seekBarMax_f1, seekBarMax_f2, seekBarMax_f3, seekBarMax_f4, seekBarMax_f5 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_control);

        seek_bar = (SeekBar)findViewById(R.id.seekBar);
        Seekbar_Text = (TextView)findViewById(R.id.SeekbarText);

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
            System.out.println("Failed to add motors to json array.");
        }

        //new ConnectBTFinger().execute();


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

        _bluetooth_ctrl = MainActivity.bluetooth_ctrl;

        seekbar();
    }


    public void seekbar(/*final TextView visibility*/){
        //seek_bar = (SeekBar)findViewById(R.id.seekBar);
        //Seekbar_Text = (TextView)findViewById(R.id.SeekbarText);
        Seekbar_Text.setText("0" + "/" + "100");
        int progress_value;

        seek_bar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        Seekbar_Text.setText(Integer.toString((int)(((float)seekBar.getProgress()/180.0)*100)) + "/" + "100");
                        //visibility.setAlpha(200 - 2*progress_value);

                        updateAngle();


                        try {
                            if (jsonMotors.get("motor1").toString() == "1" && progress_value >= seekBarMax_f1) {
                                System.out.println("array has motor1 " + seekBarMax_f1);
                                seekBarMax_f1 = progress_value;
                            }
                            if (jsonMotors.get("motor2").toString() == "1" && progress_value >= seekBarMax_f2) {
                                System.out.println("array has motor2 " + seekBarMax_f2);
                                seekBarMax_f2 = progress_value;
                            }
                            if (jsonMotors.get("motor3").toString() == "1" && progress_value >= seekBarMax_f3) {
                                System.out.println("array has motor3 " + seekBarMax_f3);
                                seekBarMax_f3 = progress_value;
                            }
                            if (jsonMotors.get("motor4").toString() == "1" && progress_value >= seekBarMax_f4) {
                                System.out.println("array has motor4 " + seekBarMax_f4);
                                seekBarMax_f4 = progress_value;
                            }
                            if (jsonMotors.get("motor5").toString() == "1" && progress_value >= seekBarMax_f5) {
                                System.out.println("array has motor5 " + seekBarMax_f5);
                                seekBarMax_f5 = progress_value;
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Seekbar_Text.setText(Integer.toString((int)(((float)seekBar.getProgress()/180.0)*100)) + "/" + "100");

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
                System.out.println("Add motor1");
                jsonMotors.remove("motor1");
                jsonMotors.put("motor1", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            f1.setImageAlpha(0);
            //motorsList.remove(1);
            try {
                System.out.println("Remove motor1");
                jsonMotors.remove("motor1");
                jsonMotors.put("motor1", 0);
                seek_bar.setProgress(0);
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
                seek_bar.setProgress(0);
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
                seek_bar.setProgress(0);
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
                seek_bar.setProgress(0);
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
                seek_bar.setProgress(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //Disconnect();

        // Send data to the website when user leaves the page
        GetToken();
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


        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        if (seekBar != null) {
            try
            {
                int progress = seekBar.getProgress();
                //textProgress.setText(Integer.toString(Integer.toString((int)(((float)progress/180.0)*100))) + "\u00b0");
                try {
                    jsonMotors.remove("angle");
                    jsonMotors.put("angle", progress);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                _bluetooth_ctrl.btSocket.getOutputStream().write(String.valueOf(jsonMotors.toString()).getBytes());
                //btSocket.getOutputStream().write(String.valueOf(jsonMotors.toString()).getBytes());
            }
           catch (/*java.io.IOException*/ Exception e)
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


    // No longer being used
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





    private void sendData() {

        final SeekBar seekProgress = (SeekBar) findViewById(R.id.seekBar);

        new Thread(new Runnable() {
            public void run() {

                InputStream in = null;
                OutputStream out = null;
                String reply = "";

                try {
                    String url = "http://10.32.8.79/sesion/api/";
                    URL object=new URL(url);

                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setRequestProperty("Content-Type", /*"text/html*/"application/json; charset=UTF-8");
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    //con.setDoInput(true);

                    JSONObject json = new JSONObject();
                    //System.out.println("test " + _username.toString());
                    //json.put("username", "test");
                    //json.put("password","prueba1234");
                    //String httpPost = command;

                    // Data format for the API
                    // now includes 'etapa'
                    // {"fecha":"2017-10-24","pulgar":20,"indice":20,"medio":50,"anular":30,"menique":30,"total":30,"username":"carlos", "paciente":"Antonio Gonzalez", "token":"550a799903bd2931b58cc577586c4fc6af7fce46"}

                    String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                    json.put("fecha", date /*"2017-11-02"*/);
                    json.put("pulgar", seekBarMax_f1 );
                    json.put("indice", seekBarMax_f2  );
                    json.put("medio", seekBarMax_f3 );
                    json.put("anular", seekBarMax_f4 );
                    json.put("menique", seekBarMax_f5 );
                    json.put("total", 0);
                    json.put("username", UserProfile.getDoctorsName() /*"carlos"*/ );
                    json.put("paciente", UserProfile.getPatientsName() /*"Antonio Gonzalez"*/);
                    json.put("token", Token.replace("\"",""));
                    json.put("etapa", UserProfile.getPatientStage());

                    out = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(json.toString());
                    writer.flush();
                    writer.close();
                    out.close();

                    // If we're interested in the reply...
                    int respCode = con.getResponseCode();
                    System.out.println("Data send response code " + respCode);


                    if (respCode == HttpURLConnection.HTTP_OK) {


                        System.out.println("Successfully sent data");

                        String line;
                        in = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null) {
                            reply += line;
                        }

                        reader.close();
                        in.close();

                        // Display the returned JSON in a text box, just for confirmation.
                        // Code not necessary for this example
                        //tv.setText(reply);
                        System.out.println("\nresponse content " + reply);
                        //Token = reply;
                        //startActivity(new Intent(LoginScreen.this, Home.class));

                    } else {
                        //Toast.makeText(getApplicationContext(),"Failed login attempt. Please try again", Toast.LENGTH_LONG).show();
                        //tv.setText("Failed to connect: " + respCode);
                        System.out.println("Failed to send data");
                    }


                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }



    private void GetToken() {

        new Thread(new Runnable() {
            public void run() {

                InputStream in = null;
                OutputStream out = null;
                String reply = "";

                try {
                    String url = "http://10.32.8.79/sesion/api/login";
                    URL object=new URL(url);

                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    //con.setDoInput(true);

                    JSONObject json = new JSONObject();
                    //System.out.println("test " + _username.toString());
                    json.put("username", UserProfile.getDoctorsName() /*"carlos"*/);
                    json.put("password", UserProfile.getUserPassword() /*"numero1234"*/);
                    //String httpPost = command;

                    out = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(json.toString());
                    writer.flush();
                    writer.close();
                    out.close();

                    // If we're interested in the reply...
                    int respCode = con.getResponseCode();
                    System.out.println("response code " + respCode);


                    if (respCode == HttpURLConnection.HTTP_OK) {

                        String line;
                        in = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null) {
                            reply += line;
                        }

                        reader.close();
                        in.close();

                        // Display the returned JSON in a text box, just for confirmation.
                        // Code not necessary for this example
                        //tv.setText(reply);
                        System.out.println("\nresponse content " + reply);
                        Token = reply.toString();

                        sendData();
                        //startActivity(new Intent(LoginScreen.this, Home.class));
                    } else {
                        //Toast.makeText(getApplicationContext(),"Failed login attempt. Please try again", Toast.LENGTH_LONG).show();
                        //tv.setText("Failed to connect: " + respCode);
                    }


                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }




}
