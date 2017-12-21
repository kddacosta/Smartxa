package com.research.deustotech.smartxa;

import android.app.Application;
import android.app.AuthenticationRequiredException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.*;
import org.w3c.dom.Text;



import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class UserProfile extends AppCompatActivity {

    public static String doctorsName;
    public static String patientsName;
    public static String patientStage;
    public static String userPassword;

    public static JSONObject UserData;

    TextView patient;
    TextView doctor;
    TextView stage;
    String Token;

    public static Context userProfileContext;

    private static final String SELECTED_ITEM = "arg_selected_item";
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* TODO: uncomment this

        patient = (TextView) findViewById(R.id.patientName);
        doctor = (TextView) findViewById(R.id.doctorName);
        stage = (TextView) findViewById(R.id.stageText);

        patient.setText(getPatientsName());
        doctor.setText(getDoctorsName());
        stage.setText(getPatientStage());

        */

        userProfileContext = getApplicationContext();

        setContentView(R.layout.activity_user_profile);
        //setContentView(R.layout.activity_menu_fragment);
        //GetToken();


       /* LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/



       //TODO: Build graph based off of stored preference values. Update values each time new data is collected

        //BuildUserStatsGraph();

        // sendData();


        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setSelectedItemId(R.id.menu_home);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(3);
        }

        selectFragment(selectedItem);


    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(3);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_home:

                frag = MenuFragment.newInstance(getString(R.string.title_home), getColorFromRes(R.color.white));
                break;
            case R.id.menu_hand_control:
                frag = HandControlFragment.newInstance(getString(R.string.menu_hand_control), getColorFromRes(R.color.white));
                break;
            case R.id.menu_finger_control:
                frag = FingerControlFragment.newInstance(getString(R.string.menu_finger_control), getColorFromRes(R.color.white));
                break;
            case R.id.menu_settings:
                frag = SettingsFragment.newInstance(getString(R.string.menu_settings), getColorFromRes(R.color.white));
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag, frag.getTag());
            //ft.add(R.id.container, frag, frag.getTag());
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }




    /*  My Code  */

    void setUserData(JSONObject userdata)
    {
        UserData = userdata;
    }
    public static void setDoctorsName (String user) {
        doctorsName = user;
    }
    public static void setPatientStage (String stage) {patientStage = stage; }
    public static void setPatientsName (String name) {
        patientsName = name;
    }
    public static void setUserPassword (String password) { userPassword = password; }

    public static String getUserPassword() { return userPassword; }
    public static String getDoctorsName () { return doctorsName; }
    public static String getPatientsName() {
        return patientsName;
    }
    public static String getPatientStage()
    {
       switch(patientStage)
       {
           case "Relapsing Remitting (RRMS)":
               return "RRMS";
               //break;
           case "Primary Progressive (PPMS)":
               return "PPMS";
               //break;

           case "Progressive Relapsing (PRMS)":
               return "PRMS";
               //break;

           case "Secondary Progressive (SPMS)":
               return "SPMS";
               //break;
           case "Default":
               return "MS";
               //break;
       }

        return patientStage;
    }



    private void sendData() {

//TODO: Add a legend to the graph

        //final SeekBar seekProgress = (SeekBar) findViewById(R.id.seekBar);

        new Thread(new Runnable() {
            public void run() {

                InputStream in = null;
                OutputStream out = null;
                String reply = "";

                try {
                    System.out.println("Trying to get data from website");

                    String url = "http://10.32.8.79/sesion/api/paciente";
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
                    // {"username": "carlos", "password": "numero1234", "paciente": "Antonio Gonzalez", "inicio": 0, "fin": 719964000}
                    //String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                    json.put("username", /*doctorsName */ "carlos" );
                    json.put("paciente", /*patientsName*/ "Antonio Gonzalez");
                    json.put("token", Token.replace("\"",""));
                    json.put("password", /*userPassword*/ "numero1234");
                    json.put("inicio", 0);
                    json.put("fin", System.currentTimeMillis());
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

                        UserData = new JSONObject(reply);

                        // Renders the graphs and inserts the collected data
                        BuildUserStatsGraph();


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
                    json.put("username", /*UserProfile.getDoctorsName()*/ "carlos");
                    json.put("password", /*UserProfile.getUserPassword()*/ "numero1234");
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


    public void BuildUserStatsGraph()
    {
        // Build the table view with the collected data

        try
        {
            //Date date = Date.from(Instant.now()); //new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Calendar.getInstance().getTime());
            GraphView graph = (GraphView) findViewById(R.id.graph);
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]
                    {

                            // {"anular": 19.533333333333335, "menique": 15.983333333333333, "medio": 18.4, "pulgar": 23.183333333333334, "indice": 20.85, "total": 61.61666666666667}
                            new DataPoint(1.0, 30.0 /*Double.parseDouble(UserData.getString("anular"))*/),
                            new DataPoint(2.0, 45.5 /*Double.parseDouble(UserData.getString("menique"))*/),
                            new DataPoint(3.0, 10.0 /*Double.parseDouble(UserData.getString("medio"))*/),
                            new DataPoint(4.0, 57.4 /*Double.parseDouble(UserData.getString("pulgar"))*/),
                            new DataPoint(5.0, 80.4 /*Double.parseDouble(UserData.getString("indice"))*/),
                            new DataPoint(6.0, 27.4 /*Double.parseDouble(UserData.getString("total"))*/),
                            // new DataPoint(0.0, 10.0),
                            // new DataPoint(1.0, 5.0)

                    });
            graph.addSeries(series);





            // styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            series.setTitle("My Past Week");

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getApplicationContext(),"stuff", Toast.LENGTH_SHORT);
                }
            });

            // activate horizontal zooming and scrolling
            graph.getViewport().setScalable(true);

            // activate horizontal scrolling
            graph.getViewport().setScrollable(true);

            // activate horizontal and vertical zooming and scrolling
            //graph.getViewport().setScalableY(true);

            // activate vertical scrolling
            //graph.getViewport().setScrollableY(true);

            series.setAnimated(true);
            series.setSpacing(5);

            // draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(R.color.colorPrimary);
            series.setValuesOnTopSize(30);

            // use static labels for horizontal and vertical labels
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(new String[] {"anular", "men.", "medio", "pulgar", "indice", "total"});
            //staticLabelsFormatter.setVerticalLabels(new String[] {"Average (%)"});
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    // TODO: Deprecate and remove the Async Task methods

    private class RequestTokenTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
           try {
               URL url = new URL("http://10.32.8.79/sesion/api/login");
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               connection.setRequestMethod("POST");
               //connection.addRequestProperty("username",username);
               //connection.addRequestProperty("password",password);
               connection.setDoOutput(true);
               connection.setConnectTimeout(5000);
               connection.setReadTimeout(5000);
               connection.connect();
               BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String content = "", line;
               while ((line = rd.readLine()) != null) {
                   content += line + "\n";
               }
               Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();
               return content;
           }
           catch (Exception e)
           {

           }
           return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here
            //displayMessage(result);
            //Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();
        }
    }


    private class PostUserDataTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://10.32.8.79/sesion/api/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("userData",UserData.toString());

                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();
                return content;
            }
            catch (Exception e)
            {

            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here
            //displayMessage(result);
            //Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();
        }
    }
}
