package com.research.deustotech.smartxa;


import android.animation.Animator;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.appyvet.materialrangebar.RangeBar;
import com.rubengees.introduction.IntroductionBuilder;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Set;


public class SettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    // Attempt to add bluetooth connection option to settings
    BluetoothAdapter bluetooth;
    private Set pairedDevices;
    public static ArrayList list = new ArrayList();
    public static String smartxaDeviceName;
    public static String smartxaAddress;
    public static Dictionary<String,String> smartxaDevice;
    public static BlueTooth bluetooth_ctrl = null;



    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    View view;
    RangeBar rangeBar;
    TextView patientText;
    Button changeButton;
    Button btoothButton;

    SharedPreferences sp;
    Spinner repSpinner;
    LottieAnimationView checkView;
    LottieAnimationView btoothCheck;
    LottieAnimationView loadView;
    TextView waitText;

    public static android.support.v4.app.Fragment newInstance(String text, int color) {
        android.support.v4.app.Fragment frag = new SettingsFragment();
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
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        changeButton = (Button) view.findViewById(R.id.changeButton);
        btoothButton = (Button) view.findViewById(R.id.btoothButton);

        changeButton.setOnClickListener(this);
        btoothButton.setOnClickListener(this);


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

        // set text and background color
        mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);


        repSpinner = (Spinner) view.findViewById(R.id.repSpinner);
        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        patientText = (TextView) view.findViewById(R.id.patientText);

        sp = this.getActivity().getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        rangeBar.setRangePinsByValue(0,sp.getInt("max_range", 100));
        repSpinner.setSelection(sp.getInt("repSpinnerId", 0));

        checkView = (LottieAnimationView)view.findViewById(R.id.animation_view);
        btoothCheck = (LottieAnimationView)view.findViewById(R.id.animation_view2);
        loadView = (LottieAnimationView)view.findViewById(R.id.animation_view3);
        waitText = (TextView)view.findViewById(R.id.waitText);


        loadView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                loadView.setProgress(0f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                System.out.println(rangeBar.getRightIndex());

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("max_range",rangeBar.getRightIndex());
                editor.apply();
                editor.commit();


            }

        });



        // TODO: Remove after testing
        //SharedPreferences.Editor editor = sp.edit();
        //editor.putString("patient_name","Antonio Gonzalez");
        //editor.apply();
        //editor.commit();

        patientText.setText(sp.getString("patient_name", "Name"));


        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        System.out.println("settings page stopped and hidden. " + sp.getString("repetitions", "5"));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("repetitions", repSpinner.getSelectedItem().toString());
        editor.putInt("repSpinnerId", repSpinner.getSelectedItemPosition());
        editor.apply();
        editor.commit();
    }


    @Override
    public void onClick(View v)
    {
       // waitText.setVisibility(View.VISIBLE);

        switch (v.getId()) {

            case R.id.changeButton:

                System.out.println("Changing the patient");

                //Toast.makeText(this.getActivity().getApplicationContext(),"Patient changed", Toast.LENGTH_LONG);

                if (!patientText.getText().toString().equals(sp.getString("patient_name", "Name"))) {
                    System.out.println("Changing the patient");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("patient_name", patientText.getText().toString());
                    editor.apply();
                    editor.commit();

                    System.out.println(sp.getString("patient_name", "Name"));
                    //Toast.makeText(this.getActivity().getApplicationContext(),"Patient changed", Toast.LENGTH_LONG);

                    checkView.playAnimation();
                }
                else
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserProfile.userProfileContext, "The patient is the same. Enter a new name to proceed.", Toast.LENGTH_LONG).show();

                        }
                    });
                }

                break;
            case R.id.btoothButton:


                //btoothCheck.playAnimation();

                System.out.println("Attempting to connect to bluetooth");

                btoothButton.setClickable(false);
                btoothButton.setBackgroundColor(getResources().getColor(R.color.grey));

                final Handler mainHandler = new Handler(UserProfile.userProfileContext.getMainLooper());

                final Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (waitText.getText().toString().equals(""))
                        {
                            System.out.println("The wait text was empty");
                            waitText.setText("Please Wait...");
                            loadView.playAnimation();
                        }
                        else
                        {
                            System.out.println("The wait text was not empty");
                            waitText.setText("");
                            loadView.cancelAnimation();
                        }



                        mainHandler.postDelayed(this,2000);
                        connectBtooth();

                        mainHandler.removeCallbacks(this);

                    } // This is your code
                };

                mainHandler.post(myRunnable);



               // connectBtooth();
//
//                btoothButton.setClickable(false);
//                System.out.println("set btooth button to unclickable");

//               new Thread(new Runnable() {
//                    public void run() {




//                    }
//                    }).start();

//                loadView.cancelAnimation();

                //btoothButton.setClickable(true);

                break;

        }
        //waitText.setVisibility(View.INVISIBLE);
    }


    private void connectBtooth()
    {


       new Thread(new Runnable() {
            public void run() {


                Looper.prepare();
                Looper mylooper = Looper.myLooper();

                try {
                    //bluetooth = BluetoothAdapter.getDefaultAdapter();

                    if (bluetooth != null /*&& bluetooth_ctrl == null*/) {


                        String status;
                        if (bluetooth.isEnabled()) {
                            String mydeviceaddress = bluetooth.getAddress();
                            String mydevicename = bluetooth.getName();
                            int state = bluetooth.getState();

                            status = mydevicename + " : " + mydeviceaddress + " : " + state + " : ";

                            //Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                            createPairedDevicesList();


                            bluetooth_ctrl = new BlueTooth(smartxaAddress, UserProfile.userProfileContext /*this.getActivity().getApplicationContext()*/ );
                            bluetooth_ctrl.ConnectBlueTooth();

//                                    try {
//                                        Thread.sleep(5000);
//                                        //waitText.setText("after sleep");
//                                    } catch (InterruptedException e) {
//                                    }
                            if (bluetooth_ctrl.isBtConnected) {

//                                        loadView.cancelAnimation();

                                btoothCheck.playAnimation();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UserProfile.userProfileContext, "Your device is now connected to Smartxa.", Toast.LENGTH_LONG).show();

                                    }
                                });


                                System.out.println("btooth is connected");
                                //Toast.makeText(this, "Successfully connected to SmartXa.", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(MainActivity.this, LoginScreen.class));

                            } else {

//                                        loadView.cancelAnimation();
                                //TODO: show a failed animation
                                System.out.println("Failed to connect btooth");

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

//                                        Handler mainHandler = new Handler(UserProfile.userProfileContext.getMainLooper());

//                                        Runnable myRunnable = new Runnable() {
//                                            @Override
//                                            public void run() {
                                                Toast.makeText(UserProfile.userProfileContext, "Failed to communicate with SmartXa. Verify that the device is powered on, " +
                                                        "then try again.", Toast.LENGTH_LONG).show();
//                                                //try{ Thread.sleep(4000); }catch(InterruptedException e){ }
//                                            } // This is your code
//                                        };
//
//                                        mainHandler.post(myRunnable);
                                    }
                                });
//                                Handler mainHandler = new Handler(UserProfile.userProfileContext.getMainLooper());
//
//                                Runnable myRunnable = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(UserProfile.userProfileContext, "Failed to communicate with SmartXa. Verify that your device is powered on, " +
//                                                "then press \"Continue\" to try again.", Toast.LENGTH_LONG).show();
//                                        //try{ Thread.sleep(4000); }catch(InterruptedException e){ }
//                                    } // This is your code
//                                };
//
//                                mainHandler.post(myRunnable);

                            }


                        } else {
                            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            //status = "BlueTooth is disabled. Smartxa is enabling bluetooth on your device.";
                            startActivityForResult(turnBTon, 1);
                            //startActivity(new Intent(MainActivity.this, LoginScreen.class));
                        }
                    }
                    else
                    {
                        System.out.println("system not bluetooth capable or bluetooth is already connected");

                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!getActivity().isFinishing()){
                                waitText.setText("");
                                loadView.cancelAnimation();
                                btoothButton.setClickable(true);
                                btoothButton.setBackgroundColor(getResources().getColor(R.color.holoBlue));
                            }
                        }
                    });


                } catch (Exception e) {
                    System.out.println("Bluetooth connection failed: " + e);
                }

            }
        }).start();


    }

    private void createPairedDevicesList()
    {

//        System.out.println("Attempting to connect to bluetooth");
//
//
//        if (waitText.getText().toString().equals(""))
//        {
//            System.out.println("The wait text was empty");
//            waitText.setText("Please Wait...");
//        }
//        else
//        {
//            System.out.println("The wait text was not empty");
//            waitText.setText("");
//        }
//
//        btoothButton.setClickable(false);
//        System.out.println("set btooth button to unclickable");



        pairedDevices = bluetooth.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for (BluetoothDevice bt: bluetooth.getBondedDevices())
            {
                // list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                //list.add(bt.getAddress());
                //smartxaDevice.put(bt.getName(),bt.getAddress());
                smartxaDeviceName = bt.getName();
                smartxaAddress = bt.getAddress();
                System.out.println(smartxaAddress + " " + smartxaDeviceName);
                //Toast.makeText(getApplicationContext(),"Paired Device: " + bt.getName() + " " + bt.getAddress(), Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
           // Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        // devicelist.setAdapter(adapter);
        //devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }


}
