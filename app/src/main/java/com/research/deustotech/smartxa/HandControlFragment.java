package com.research.deustotech.smartxa;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.appyvet.materialrangebar.RangeBar;

import org.json.JSONObject;

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
import java.util.UUID;


public class HandControlFragment extends android.support.v4.app.Fragment {


    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    View view;
    RangeBar rangeBar;
    SharedPreferences sp;

    LottieAnimationView pointer;

    TextView goodjob;
    TextView btoothWarning;

    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    private String address = MainActivity.smartxaAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    JSONObject jsonMotors = new JSONObject();

    SeekBar seek_bar;

    public int seekBarMax = 0;
    public int rangBarMax = 0;

    BlueTooth _bluetooth_ctrl = null;

    SeekBar seekBar;
    TextView textProgress;
    TextView repProgress;
    TextView totalRepText;
    int repCounter = 0;
    Button updateButton;
    ToggleButton modeButton;
    private String Token;

    Switch modeSwitch;


    private static final String SELECTED_ITEM = "arg_selected_item";
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;





    public static android.support.v4.app.Fragment newInstance(String text, int color) {
        android.support.v4.app.Fragment frag = new HandControlFragment();
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

        view = inflater.inflate(R.layout.fragment_hand_control, container, false);
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
        pointer = (LottieAnimationView) view.findViewById(R.id.animation_view);
        btoothWarning = (TextView) view.findViewById(R.id.btoothWarning);

        // set text and background color
        mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);

        sp = this.getActivity().getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        // Set the max value of the control equal to the specidied value in the preferences
        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        rangeBar.setTickEnd(sp.getInt("max_range", 100));

        // Start the range bar at 0
        rangeBar.setRangePinsByIndices(0,0);

        // Create object references
        seekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        textProgress = (TextView) view.findViewById(R.id.rangeText);
        repProgress = (TextView) view.findViewById(R.id.repText);
        totalRepText = (TextView) view.findViewById(R.id.totalRepsText);
        goodjob = (TextView)view.findViewById(R.id.goodjob);

        //modeButton = (ToggleButton) findViewById(R.id.modeButton);
        //updateButton = (Button) findViewById(R.id.updatebutton);
        //updateButton.setActivated(false);
        //new ConnectBTFullHand().execute();
        _bluetooth_ctrl = SettingsFragment.bluetooth_ctrl;


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

        totalRepText.setText(/*"/ " + */sp.getString("repetitions", "5"));



        // Initiate listener functions
        seekbar();
        //switchBarListener();




        // Prompt the user to select a control mode
        //showModeDialog();

        pointer.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                goodjob.setText("Good Job!");
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                pointer.setProgress(0f);
                goodjob.setText("");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        // show btooth warning
        if(_bluetooth_ctrl == null)
        {
            System.out.println("btooth control is null");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserProfile.userProfileContext, "Your device is not connected to Smartxa. Go to settings and select \"Connect to Smartxa\"", Toast.LENGTH_LONG).show();

                }
            });

            btoothWarning.setVisibility(View.VISIBLE);
        }
        else if (_bluetooth_ctrl.isBtConnected)
        {
            System.out.println("btooth control is not null");
            btoothWarning.setVisibility(View.INVISIBLE);
        }


    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }




    /*

    public void showModeDialog()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()){
                    new AlertDialog.Builder(FullHandControl.this)
                            .setTitle("Full Hand Control")
                            .setMessage("Select a Mode to get started.")
                            .setCancelable(false)
                            .setPositiveButton("Enter Freemode", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })
                            .setNegativeButton("Start a Test",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    modeSwitch.setChecked(true);
                                }
                            }).show();

                }
            }
        });
    }
*/


/*
    public void switchBarListener()
    {
        modeSwitch = (Switch) view.findViewById(R.id.modeSwitch);

        modeSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (modeSwitch.isChecked())
                        {
                            System.out.println("mode is checked");
                            // is checked == test mode
                        }
                        else
                        {
                            System.out.println("mode is not checked");
                            // is not checked == free mode
                        }
                    }
                }
        );
    }
*/


    public void seekbar(/*final TextView visibility*/) {
        //seek_bar = (SeekBar) view.findViewById(R.id.seekBar2);

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            int progress_value;
            int comprobacion = 0;

            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {

                // show btooth warning
                if(_bluetooth_ctrl == null)
                {
                    System.out.println("btooth control is null");
                    btoothWarning.setVisibility(View.VISIBLE);
                }
                else if (_bluetooth_ctrl.isBtConnected)
                {
                    System.out.println("btooth control is not null");
                    btoothWarning.setVisibility(View.INVISIBLE);


                    updateAngle();

                    //System.out.println(repProgress.getText().toString() + " " + totalRepText.getText().toString().replace("/", ""));


                    if (repProgress.getText().toString().equals(totalRepText.getText().toString())) {
                        System.out.println("Finished reps " + repProgress.getText().toString() + totalRepText.getText().toString().replace("/", ""));
                        //TODO: Notify user that test is finished.

                        GetToken();
                        repProgress.setText(Integer.toString(0));
                        repCounter = 0;

                        pointer.playAnimation();
                    }

                    // Get the max value for the angle
                    if (rightPinIndex >= rangBarMax)
                    {
                        rangBarMax = rightPinIndex;
                        //GetToken();

                    }
                    if (rightPinIndex>5){
                        comprobacion = 1;
                    }

                    // Update the rep count
                    if (rangBarMax > 5 && rightPinIndex < 5 && comprobacion == 1)
                    {
                        repCounter++;
                        repProgress.setText(Integer.toString(repCounter));
                        comprobacion = 0;


                    }
                }





            }

        });


    }







    // Send bluetooth data to the raspberry pi: which motor, and the angle
    private void updateAngle() {


        if (textProgress != null && rangeBar != null) {
            try
            {
                //int progress = seekBar.getProgress();
                int progress = (int)(((double)rangeBar.getRightIndex() / 100) * 180);
                textProgress.setText(Integer.toString(rangeBar.getRightIndex()));

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

        //TODO: Replace seekbar with other bar
        final SeekBar seekProgress = (SeekBar) view.findViewById(R.id.seekBar2);
        final RangeBar rangeBarProgress = (RangeBar) view.findViewById(R.id.rangebar);
        final TextView repProgress = (TextView) view.findViewById(R.id.repText);

        int progress = rangeBarProgress.getRightIndex();
        //int progress = seekProgress.getProgress();
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
                    //System.out.println(System.currentTimeMillis());

                    String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                    json.put("fecha", date /*"2017-11-28"*/);
                    json.put("pulgar", 0);
                    json.put("indice", 0 );
                    json.put("medio", 0);
                    json.put("anular", 0);
                    json.put("menique", 0);
                    json.put("total", /*(int)(((float)seekBarMax/180.0)*100)*/ rangBarMax );
                    //System.out.println((int)(((float)50/180.0)*100) );
                    System.out.println(MenuFragment.getDoctorsName());
                    System.out.println(sp.getString("patient_name","Antonio Gonzalez"));
                    json.put("username", MenuFragment.getDoctorsName() /*"carlos"*/ );
                    json.put("paciente", sp.getString("patient_name", "Antonio Gonzalez")  /*"Antonio Gonzalez"*/ );
                    json.put("token", Token.replace("\"",""));
                    json.put("etapa", MenuFragment.getPatientStage() /*"Test"*/);
                    json.put("repeticiones", repCounter);
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

}
