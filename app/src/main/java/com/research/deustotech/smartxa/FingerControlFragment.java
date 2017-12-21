package com.research.deustotech.smartxa;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;


public class FingerControlFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    View view;
    TextView btoothWarning;


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
    RangeBar rangeBar;
    String Token;

    SharedPreferences sp;
    BlueTooth _bluetooth_ctrl = null;

    public int seekBarMax_f1, seekBarMax_f2, seekBarMax_f3, seekBarMax_f4, seekBarMax_f5 = 0;



    public static android.support.v4.app.Fragment newInstance(String text, int color) {
        android.support.v4.app.Fragment frag = new FingerControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_COLOR, color);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_finger_control, container, false);


        f1 = (ImageButton) view.findViewById(R.id.finger1);
        f2 = (ImageButton) view.findViewById(R.id.finger2);
        f3 = (ImageButton) view.findViewById(R.id.finger3);
        f4 = (ImageButton) view.findViewById(R.id.finger4);
        f5 = (ImageButton) view.findViewById(R.id.finger5);

        f1.setImageAlpha(0);
        f2.setImageAlpha(0);
        f3.setImageAlpha(0);
        f4.setImageAlpha(0);
        f5.setImageAlpha(0);

        f1.setOnClickListener(this);
        f2.setOnClickListener(this);
        f3.setOnClickListener(this);
        f4.setOnClickListener(this);
        f5.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
            mColor = args.getInt(ARG_COLOR);
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
            mColor = savedInstanceState.getInt(ARG_COLOR);
        }

        // initialize views
        mContent = view.findViewById(R.id.fragment_content);
        mTextView = (TextView) view.findViewById(R.id.text);
        btoothWarning = (TextView) view.findViewById(R.id.btoothWarning);

        // set text and background color
        mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);

        sp = this.getActivity().getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);


        //seek_bar = (SeekBar)view.findViewById(R.id.seekBar);
        rangeBar = (RangeBar)view.findViewById(R.id.rangebar);
        Seekbar_Text = (TextView)view.findViewById(R.id.SeekbarText);

        rangeBar.setTickEnd(sp.getInt("max_range", 100));

        // Start the range bar at 0
        rangeBar.setRangePinsByIndices(0,0);


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

        System.out.println(jsonMotors);
        //new ConnectBTFinger().execute();

/*
        f1 = (ImageButton) view.findViewById(R.id.finger1);
        f2 = (ImageButton) view.findViewById(R.id.finger2);
        f3 = (ImageButton) view.findViewById(R.id.finger3);
        f4 = (ImageButton) view.findViewById(R.id.finger4);
        f5 = (ImageButton) view.findViewById(R.id.finger5);

        f1.setImageAlpha(0);
        f2.setImageAlpha(0);
        f3.setImageAlpha(0);
        f4.setImageAlpha(0);
        f5.setImageAlpha(0);
*/


        _bluetooth_ctrl = SettingsFragment.bluetooth_ctrl;


        // show btooth warning
        if(_bluetooth_ctrl == null)
        {
            System.out.println("btooth control is null");

            btoothWarning.setVisibility(View.VISIBLE);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserProfile.userProfileContext, "Your device is not connected to Smartxa. Go to settings and select \"Connect to Smartxa\"", Toast.LENGTH_LONG).show();

                }
            });
        }
        else if (_bluetooth_ctrl.isBtConnected)
        {
            System.out.println("btooth control is not null");
            btoothWarning.setVisibility(View.INVISIBLE);
        }


        seekbar();

    }




    @Override
    public void onClick(View v) {

        System.out.println("Button clicked");
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.finger1:
                if(f1.getImageAlpha() == 0){
                    f1.setImageAlpha(255);
                    //motorsList.add(1);
                    try {
                        System.out.println("Add motor1");
                        jsonMotors.remove("motor1");
                        jsonMotors.put("motor1", 1);
                        System.out.println(jsonMotors);
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
                        System.out.println(jsonMotors);
                        rangeBar.setRangePinsByIndices(0,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.finger2:
                if(f2.getImageAlpha() == 0){
                    f2.setImageAlpha(255);
                    //motorsList.add(2);
                    try {
                        System.out.println("Add motor2");
                        jsonMotors.remove("motor2");
                        jsonMotors.put("motor2", 1);
                        System.out.println(jsonMotors);
                    } catch (Exception e) {
                        System.out.println("failed to add motor 2");
                        e.printStackTrace();
                    }
                }else {
                    f2.setImageAlpha(0);
                    //motorsList.remove(2);
                    try {
                        System.out.println("Remove motor2");
                        jsonMotors.remove("motor2");
                        jsonMotors.put("motor2", 0);
                        System.out.println(jsonMotors);
                        rangeBar.setRangePinsByIndices(0,0);
                    } catch (Exception e) {
                        System.out.println("failed to remove motor 2");
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.finger3:
                if(f3.getImageAlpha() == 0){
                    f3.setImageAlpha(255);
                    //motorsList.add(3);
                    try {
                        System.out.println("Add motor3");
                        jsonMotors.remove("motor3");
                        jsonMotors.put("motor3", 1);
                        System.out.println(jsonMotors);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    f3.setImageAlpha(0);
                    //motorsList.remove(3);
                    try {
                        System.out.println("Remove motor3");
                        jsonMotors.remove("motor3");
                        jsonMotors.put("motor3", 0);
                        System.out.println(jsonMotors);
                        rangeBar.setRangePinsByIndices(0,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.finger4:
                if(f4.getImageAlpha() == 0){
                    f4.setImageAlpha(255);
                    //motorsList.add(4);
                    try {
                        System.out.println("Add motor4");
                        jsonMotors.remove("motor4");
                        jsonMotors.put("motor4", 1);
                        System.out.println(jsonMotors);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    f4.setImageAlpha(0);
                    //motorsList.remove(4);
                    try {
                        System.out.println("Remove motor4");
                        jsonMotors.remove("motor4");
                        jsonMotors.put("motor4", 0);
                        System.out.println(jsonMotors);
                        rangeBar.setRangePinsByIndices(0,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.finger5:
                if(f5.getImageAlpha() == 0){
                    f5.setImageAlpha(255);
                    //motorsList.add(5);
                    try {
                        System.out.println("Add motor5");
                        jsonMotors.remove("motor5");
                        jsonMotors.put("motor5", 1);
                        System.out.println(jsonMotors);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    f5.setImageAlpha(0);
                    //motorsList.remove(5);
                    try {
                        System.out.println("Remove motor5");
                        jsonMotors.remove("motor5");
                        jsonMotors.put("motor5", 0);
                        System.out.println(jsonMotors);
                        rangeBar.setRangePinsByIndices(0,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }



    public void seekbar(/*final TextView visibility*/){
        //seek_bar = (SeekBar)findViewById(R.id.seekBar);
        //Seekbar_Text = (TextView)findViewById(R.id.SeekbarText);
        Seekbar_Text.setText("0" + "/" + (int)(((double)rangeBar.getTickEnd() / 100) * 100));
        int progress_value;


        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            int progress_value;

            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                progress_value = rightPinIndex;
                Seekbar_Text.setText(Integer.toString(rightPinIndex) + "/" + (int)(((double)rangeBar.getTickEnd() / 100) * 100));
                updateAngle();


                try {
                    if (jsonMotors.get("motor1").equals(1) && progress_value >= seekBarMax_f1) {
                        System.out.println("array has motor1 " + seekBarMax_f1);
                        seekBarMax_f1 = progress_value;
                    }
                    if (jsonMotors.get("motor2").equals(1) && progress_value >= seekBarMax_f2) {
                        System.out.println("array has motor2 " + seekBarMax_f2);
                        seekBarMax_f2 = progress_value;
                    }
                    if (jsonMotors.get("motor3").equals(1) && progress_value >= seekBarMax_f3) {
                        System.out.println("array has motor3 " + seekBarMax_f3);
                        seekBarMax_f3 = progress_value;
                    }
                    if (jsonMotors.get("motor4").equals(1) && progress_value >= seekBarMax_f4) {
                        System.out.println("array has motor4 " + seekBarMax_f4);
                        seekBarMax_f4 = progress_value;
                    }
                    if (jsonMotors.get("motor5").equals(1) && progress_value >= seekBarMax_f5) {
                        System.out.println("array has motor5 " + seekBarMax_f5);
                        seekBarMax_f5 = progress_value;
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }

        });


/*
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

*/

    }




/*
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

*/

    private void updateAngle() {


        //SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        if (rangeBar != null) {
            try
            {
                int progress = (int)(((double)rangeBar.getRightIndex() / 100) * 180);
                System.out.println(progress);
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



    private void sendData() {

        final SeekBar seekProgress = (SeekBar) view.findViewById(R.id.seekBar);

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
                    json.put("pulgar", (seekBarMax_f1 ));
                    System.out.println(seekBarMax_f1);
                    System.out.println(seekBarMax_f2);
                    System.out.println(seekBarMax_f3);
                    System.out.println(seekBarMax_f4);
                    System.out.println(seekBarMax_f5);
                    json.put("indice", (seekBarMax_f2 ));
                    json.put("medio", (seekBarMax_f3 ));
                    json.put("anular", (seekBarMax_f4 ));
                    json.put("menique", (seekBarMax_f5 ));
                    json.put("total", 0);
                    json.put("username", MenuFragment.getDoctorsName() /*"carlos"*/ );
                    System.out.println(MenuFragment.getDoctorsName());
                    json.put("paciente", sp.getString("patient_name", "Antonio Gonzalez") /*"Antonio Gonzalez"*/);
                    System.out.println(sp.getString("patient_name", ""));
                    json.put("token", Token.replace("\"",""));
                    json.put("etapa", MenuFragment.getPatientStage());
                    json.put("repeticiones",1);

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
                    json.put("username", MenuFragment.getDoctorsName() /*"carlos"*/);
                    json.put("password", MenuFragment.getUserPassword() /*"numero1234"*/);
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


    @Override
    public void onStop()
    {
        super.onStop();
        // show btooth warning
        if(_bluetooth_ctrl == null)
        {
            System.out.println("btooth control is null");

            btoothWarning.setVisibility(View.VISIBLE);
        }
        else
        {
            System.out.println("btooth control is not null");
            btoothWarning.setVisibility(View.INVISIBLE);

            GetToken();
        }



    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }


}
