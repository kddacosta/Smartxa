package com.research.deustotech.smartxa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.FractionRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kori on 12/8/17.
 */

public class MenuFragment extends android.support.v4.app.Fragment {

    public static String doctorsName;
    public static String patientsName;
    public static String patientStage;
    public static String userPassword;

    public static JSONObject UserData;

    TextView patient;
    TextView doctor;
    TextView stage;
    String Token;

    View view;

    SharedPreferences sp;

    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    public static android.support.v4.app.Fragment newInstance(String text, int color) {
        android.support.v4.app.Fragment frag = new MenuFragment();
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
        view = inflater.inflate(R.layout.activity_menu_fragment, container, false);
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

        sp = this.getActivity().getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        // initialize views
        mContent = view.findViewById(R.id.fragment_content);
        mTextView = (TextView) view.findViewById(R.id.text);

        // set text and background color
        mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);


        patient = (TextView) view.findViewById(R.id.patientName);
        doctor = (TextView) view.findViewById(R.id.doctorName);
        stage = (TextView) view.findViewById(R.id.stageText);

        patient.setText(sp.getString("patient_name", ""));
        doctor.setText(getDoctorsName());
        stage.setText(getPatientStage());



        //TODO: Build graph based off of stored preference values. Update values each time new data is collected

        GetToken();
        //BuildUserStatsGraph();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }



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

                    json.put("username", doctorsName /*"carlos"*/ );
                    json.put("paciente", sp.getString("patient_name", "Antonio Gonzalez")  /*"Antonio Gonzalez"*/);
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
                    json.put("username", /*MenuFragment.getDoctorsName()*/ "carlos");
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
            GraphView graph = (GraphView) view.findViewById(R.id.graph);
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]
                    {

                            // {"anular": 19.533333333333335, "menique": 15.983333333333333, "medio": 18.4, "pulgar": 23.183333333333334, "indice": 20.85, "total": 61.61666666666667}
                            new DataPoint(1.0, Double.parseDouble(UserData.getString("anular"))),
                            new DataPoint(2.0, Double.parseDouble(UserData.getString("menique"))),
                            new DataPoint(3.0, Double.parseDouble(UserData.getString("medio"))),
                            new DataPoint(4.0, Double.parseDouble(UserData.getString("pulgar"))),
                            new DataPoint(5.0, Double.parseDouble(UserData.getString("indice"))),
                            new DataPoint(6.0, Double.parseDouble(UserData.getString("total"))),
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


            series.setTitle("My Past Week ( Ave. %)");

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                   // Toast.makeText(getApplicationContext(),"stuff", Toast.LENGTH_SHORT);
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
            staticLabelsFormatter.setHorizontalLabels(new String[] {"Anular", "Men.", "Medio", "Pulgar", "Indice", "Mano"});
            //staticLabelsFormatter.setVerticalLabels(new String[] {"Average (%)"});
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
