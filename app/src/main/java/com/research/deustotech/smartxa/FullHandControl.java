package com.research.deustotech.smartxa;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seekcircle.SeekCircle;

import android.bluetooth.*;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.json.*;
import java.lang.Math;

//import static android.provider.Settings.Secure.ANDROID_ID;

public class FullHandControl extends AppCompatActivity {

    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    private String address = MainActivity.smartxaAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    JSONObject jsonMotors = new JSONObject();

    SeekBar seek_bar;

    public int seekBarMax = 0;

    BlueTooth _bluetooth_ctrl;

    SeekBar seekBar;
    TextView textProgress;
    Button updateButton;
    ToggleButton modeButton;
    private String Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_hand_control);

        try {
            //jsonMotors.put("motor1", 0);
            //jsonMotors.put("motor2", 0);
            //jsonMotors.put("motor3", 0);
            //jsonMotors.put("motor4", 0);
            //jsonMotors.put("motor5", 0);
            jsonMotors.put("motor6", 1);
            jsonMotors.put("angle", 0);

        } catch (Exception e) {

        }



        seekbar();

        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        textProgress = (TextView) findViewById(R.id.textView4);

        modeButton = (ToggleButton) findViewById(R.id.modeButton);

        updateButton = (Button) findViewById(R.id.updatebutton);
        updateButton.setActivated(false);

        //new ConnectBTFullHand().execute();
        _bluetooth_ctrl = MainActivity.bluetooth_ctrl;
    }


    //TODO: Create new thread. Pause thread. Send data every minute or 10 seconds or something
    // calculate the max of the progress bar
    public void seekbar(/*final TextView visibility*/) {
        seek_bar = (SeekBar) findViewById(R.id.seekBar2);


        seek_bar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        //  Seekbar_Text.setText(progress + "/" + seek_bar.getMax());
                        //visibility.setAlpha(200 - 2*progress_value);

                        if (modeButton.isChecked())
                        {
                            //final SeekBar seekProgress = (SeekBar) findViewById(R.id.seekBar2);
                            //int progress = seekProgress.getProgress();
                            updateAngle();

                            if (progress > seekBarMax)
                            {
                                seekBarMax = progress;
                                //GetToken();
                            }

                        }
                        else
                        {
                           // int seekbarProgress = seekBar.getProgress(); //seekCircle.getProgress();
                           // textProgress.setText(Integer.toString((int)(((float)progress/180.0)*100))); /*+ "\u00b0");*/

                            updateAngle();
                            if (progress > seekBarMax)
                            {
                                seekBarMax = progress;
                                //GetToken();
                            }

                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Seekbar_Text.setText(progress_value + "/" + seek_bar.getMax());

                    }
                }
        );

    }

        //updateAngle();


    public void modeButtonClicked(View v)
    {

        if (modeButton.isChecked())
        {
            updateButton.setActivated(false);
            Toast.makeText(getApplicationContext(), "Set to Auto control mode. Slider can be adjusted freely", Toast.LENGTH_LONG).show();
        }
        else
        {
            updateButton.setActivated(true);
            Toast.makeText(getApplicationContext(), "Set to Manual control mode. Press update to make changes", Toast.LENGTH_LONG).show();
        }
    }

    public void updateButtonClicked(View v)
    {
        updateAngle();
        //GetToken();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //Disconnect();

        GetToken();
    }

    private void updateAngle() {


        if (textProgress != null && seekBar != null) {
            try
            {
                int progress = seekBar.getProgress();
                textProgress.setText(Integer.toString((int)(((float)progress/180.0)*100)));

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
                //msg("Error");
            }
        }
        finish(); //return to the first layout

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    private void sendData() {

        final SeekBar seekProgress = (SeekBar) findViewById(R.id.seekBar2);

        int progress = seekProgress.getProgress();
        //if (progress > seekBarMax)
       // {
           // seekBarMax = progress;

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
                    json.put("fecha", "2017-11-28");
                    json.put("pulgar", 0);
                    json.put("indice", 0 );
                    json.put("medio", 0);
                    json.put("anular", 0);
                    json.put("menique", 0);
                    json.put("total", seekProgress.getProgress() );
                    json.put("username", "carlos" );
                    json.put("paciente", "Antonio Gonzalez" );
                    json.put("token", Token.replace("\"",""));
                    //json.put("etapa", "");
                    //{"fecha":"2017-10-24","pulgar":20,"indice":20,"medio":50,"anular":30,"menique":30,"total":30,"username":"carlos", "paciente":"Antonio Gonzalez", "token":"550a799903bd2931b58cc577586c4fc6af7fce46"}
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
                        System.out.println("Token from request" + Token.toString());
                        // String resp = reply;
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

        //}

    }



    private void GetToken() {


        // TODO: If token response is expired, then request a token again

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
                    json.put("username", "carlos");
                    json.put("password","numero1234");
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

                        // Token = "";

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


    // No longer being used
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