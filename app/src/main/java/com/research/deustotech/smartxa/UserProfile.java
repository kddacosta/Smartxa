package com.research.deustotech.smartxa;

import android.app.AuthenticationRequiredException;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import org.w3c.dom.Text;


public class UserProfile extends AppCompatActivity {

    public static String doctorsName;
    public static String patientsName;
    public static String patientStage;

    public static JSONObject UserData = new JSONObject();

    TextView patient;
    TextView doctor;
    TextView stage;
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

    }


    void setUserData(JSONObject userdata)
    {
        UserData = userdata;
    }
    public static void setDoctorsName (String user) {
        doctorsName = user;
    }
    public static void setPatientStage (String stage) {patientStage = stage; }
    public static void setPatientsName (String pass) {
        patientsName = pass;
    }

    String getDoctorsName () {return doctorsName;}
    String getPatientsName() {
        return patientsName;
    }
    String getPatientStage() {return patientStage; }



    public static void getToken()
    {
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
                    json.put("username", doctorsName.toString());// "test"
                    json.put("password",patientsName.toString());//"prueba1234"
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
                        //startActivity(new Intent(LoginScreen.this, Home.class));
                    } else {
                       // Toast.makeText(getApplicationContext(),"Failed login attempt. Please try again", Toast.LENGTH_LONG).show();
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
