package com.research.deustotech.smartxa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.bluetooth.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.paolorotolo.appintro.*;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.Slide;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.prefs.Preferences;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter(); //null;
    private Set pairedDevices;
    public static ArrayList list = new ArrayList();
    public static String smarxaDeviceName;
    public static String smartxaAddress;
    public static Dictionary<String,String> smartxaDevice;
    public static BlueTooth bluetooth_ctrl;
    ProgressBar loadBar;
    public static Button continueBtn;

    public static Context mainActivityContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.smartxa_toolbar);
        //setSupportActionBar(myToolbar);
        //ActionBar smartxa_bar = getSupportActionBar();

       // myToolbar.setTitle("Smartxa");

        //smartxa_bar.setDisplayHomeAsUpEnabled(true);
        //smartxa_bar.setHomeButtonEnabled(true);

        mainActivityContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        //loadBar = (ProgressBar) findViewById(R.id.progressBar_load);
        //loadBar.setAlpha(0);
        //BlueTooth.Disconnect();
        continueBtn = (Button) findViewById(R.id.continuebutton);

        //System.out.println(System.currentTimeMillis());




        //TODO: adding an intro page to the application
        SharedPreferences sp = getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        if (!sp.getBoolean("second", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("second", true);

            editor.apply();
            editor.commit();

            //new IntroductionBuilder(this);
            //new IntroductionBuilder(this).withSlides(generateSlides());
            //TODO: edit intro slides
           // new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();

            //Intent intent = new Intent(this, IntroSlides.class); // Call the AppIntro java class
            //startActivity(intent);

            //TODO: remove after testing
            editor.remove("second");
            editor.clear();
            editor.apply();
            editor.commit();
        }

    }


    private List<Slide> generateSlides() {
        List<Slide> result = new ArrayList<>();

        result.add(new Slide()
                .withTitle("Some title")
                .withDescription("Some description").
                        withColorResource(R.color.green)
                //.withImage(R.drawable.myImage)
        );

        result.add(new Slide()
                        .withTitle("Another title")
                        .withDescription("Another description")
                        .withColorResource(R.color.indigo)
                //.withImage(R.drawable.myImage2)
        );

        result.add(new Slide()
                        .withTitle("Some title")
                        .withDescription("Some description").
                                withColorResource(R.color.colorPrimary)
                //.withImage(R.drawable.myImage)
        );

        result.add(new Slide()
                        .withTitle("Another title")
                        .withDescription("Another description")
                        .withColorResource(R.color.red)
                //.withImage(R.drawable.myImage2)
        );

        return result;
    }

    public void continueButtonClicked(View v)
    {

        // System.out.println(System.currentTimeMillis());

        //Toast.makeText(MainActivity.this, "Connecting to SmartXa. Please wait...", Toast.LENGTH_SHORT).show();
        //try{ Thread.sleep(3000); }catch(InterruptedException e){ }
        //Toast.makeText(getApplicationContext(), "Connecting to SmartXa. Please wait...", Toast.LENGTH_LONG).show();
        //startActivity(new Intent(MainActivity.this, LoginScreen.class));

        //BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        //TextView textview = (TextView)findViewById(R.id.textView);

        //BlueTooth smartxaBluetooth = new BlueTooth();
        //smartxaBluetooth.bluetooth = BluetoothAdapter.getDefaultAdapter();

        startActivity(new Intent(MainActivity.this, LoginScreen.class));
/*
      //TODO: This is the main function to execute. uncomment after testing.

        if(bluetooth != null)
        {

            String status;
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                int state = bluetooth.getState();

                status = mydevicename + " : " + mydeviceaddress + " : " + state + " : ";

                //Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                createPairedDevicesList();

                continueBtn.setClickable(false);



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()){
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Contacting Smartxa")
                                    .setMessage("Press 'Conectar' to continue")
                                    .setCancelable(false)
                                    .setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Whatever...
                                            bluetooth_ctrl = new BlueTooth();
                                            bluetooth_ctrl.ConnectBlueTooth();

                                            try{ Thread.sleep(5000); }catch(InterruptedException e){ }
                                            if (bluetooth_ctrl.isBtConnected)
                                            {
                                                //Toast.makeText(this, "Successfully connected to SmartXa.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, LoginScreen.class));
                                            }
                                            else
                                            {
                                                Handler mainHandler = new Handler(mainActivityContext.getMainLooper());

                                                Runnable myRunnable = new Runnable() {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(mainActivityContext, "Failed to communicate with SmartXa. Verify that your device is powered on, " +
                                                                "then press \"Continue\" to try again.", Toast.LENGTH_SHORT).show();
                                                        //try{ Thread.sleep(4000); }catch(InterruptedException e){ }
                                                    } // This is your code
                                                };

                                                mainHandler.post(myRunnable);

                                                //Toast.makeText(mainActivityContext,"Failed to communicate with SmartXa. Verify that your device is powered on, then press \\'Continue\\'\" +\n" +
                                                //        "\" to try again.",Toast.LENGTH_LONG);

                                            }
                                        }
                                    }).show();
                        }
                    }
                });


            }
            else
            {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                status = "BlueTooth is disabled. Smartxa is enabling bluetooth on your device.";
                startActivityForResult(turnBTon,1);
                //startActivity(new Intent(MainActivity.this, LoginScreen.class));
            }

            //bluetooth.listenUsingRfcommWithServiceRecord("raspberrypi", new UUID());

        }

        else
        {
            Toast.makeText(this, "This device is not bluetooth capable", Toast.LENGTH_LONG).show();

        }

*/

    }


    public void openLoginScreen()
    {
        startActivity(new Intent(MainActivity.this, LoginScreen.class));
    }


    private void createPairedDevicesList()
    {
        pairedDevices = bluetooth.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for (BluetoothDevice bt: bluetooth.getBondedDevices())
            {
               // list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                //list.add(bt.getAddress());
                //smartxaDevice.put(bt.getName(),bt.getAddress());
                smarxaDeviceName = bt.getName();
                smartxaAddress = bt.getAddress();

                //Toast.makeText(getApplicationContext(),"Paired Device: " + bt.getName() + " " + bt.getAddress(), Toast.LENGTH_SHORT).show();
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


