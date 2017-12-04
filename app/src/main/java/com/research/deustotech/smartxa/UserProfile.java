package com.research.deustotech.smartxa;

import android.app.AuthenticationRequiredException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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


public class UserProfile extends AppCompatActivity {

    public static String doctorsName;
    public static String patientsName;
    public static String patientStage;
    public static String userPassword;

    public static JSONObject UserData = new JSONObject();

    TextView patient;
    TextView doctor;
    TextView stage;
    String Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        patient = (TextView) findViewById(R.id.patientName);
        doctor = (TextView) findViewById(R.id.doctorName);
        stage = (TextView) findViewById(R.id.stageText);

        patient.setText(getPatientsName());
        doctor.setText(getDoctorsName());
        stage.setText(getPatientStage());

        //Date date = Date.from(Instant.now()); //new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Calendar.getInstance().getTime());
        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]
        {
                new DataPoint(1,10),
                new DataPoint(2,20),
                new DataPoint(3,5),
                new DataPoint(4,15)

        });

       /* LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/
        graph.addSeries(series);

        sendData();
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
                    json.put("paciente", patientsName /*"Antonio Gonzalez"*/);
                    json.put("token", Token.replace("\"",""));
                    json.put("password", userPassword /*"numero1234"*/);
                    json.put("inicio", 0);
                    json.put("fin", 0);
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
