package com.research.deustotech.smartxa;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginScreen extends AppCompatActivity {

    private String username;
    private String password;
    private String response;

    Button loginButton;
    RequestTokenTask request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        loginButton = (Button) findViewById(R.id.authbutton);

        request = new RequestTokenTask();
        //request.execute();
    }


    public void loginButtonClicked(View v)
    {

        try
        {
            TextView user = (TextView) findViewById(R.id.UsernameText);
            TextView pass = (TextView) findViewById(R.id.PasswordText2);
            username = user.getText().toString();
            password = pass.getText().toString();

            //UserProfile userProfile = new UserProfile();
            //userProfile.setUsername(username);
            //userProfile.setPassword(password);

            sendCommand(username, password);
            //request.execute();
            //loginButton.setActivated(true);
            //Toast.makeText(getApplicationContext(),"continued",Toast.LENGTH_LONG).show();
            //startActivity(new Intent(LoginScreen.this, Home.class));

            // TODO: POST request to get auth token from api
            // store token in var
            // Use token in app to send data to api

        }
        catch (Exception e)
        {
            System.out.println("Failed" + e);
            // log error. failed to communicate with api: + e
        }
    }





    private void sendCommand(final String _username, final String _password) {
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
                    json.put("username", _username.toString());// "test"
                    json.put("password",_password.toString());//"prueba1234"
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
                        startActivity(new Intent(LoginScreen.this, Home.class));
                    } else {
                        Toast.makeText(getApplicationContext(),"Failed login attempt. Please try again", Toast.LENGTH_LONG).show();
                        //tv.setText("Failed to connect: " + respCode);
                    }

                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }






    private class RequestTokenTask extends AsyncTask<Void, Void, Void> {


        HttpURLConnection connection;

        @Override
        protected void onPreExecute() {

            //loginButton.setActivated(false);
            System.out.println("Attempt to login");
            //Toast.makeText(getApplicationContext(),"Attempting Login", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... urls) {

            //Toast.makeText(getApplicationContext(),"Getting token", Toast.LENGTH_LONG).show();


            try {
                System.out.println("Code is here");
                //Toast.makeText(getApplicationContext(),"Attempting Login", Toast.LENGTH_LONG).show();
                URL url = new URL("http://10.32.8.79/sesion/api/login");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.addRequestProperty("username", "test"); // username);
                connection.addRequestProperty("password", "prueba1234"); // password);

                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);  //This line causes the app to crash
                connection.setReadTimeout(5000);



                connection.connect();




                System.out.println(connection.getContent());

                //BufferedReader br = new BufferedReader(new InputStreamReader(
                //        (connection.getInputStream())));

                /*
                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }

                connection.disconnect();


                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                */
                System.out.println("now here");
                //Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
                //response = content;
                //return content;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Request failed: " + e, Toast.LENGTH_LONG).show();

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {


            super.onPostExecute(result);

            System.out.println("onPostExecute");
            // this is executed on the main thread after the process is over
            // update your UI here
            //displayMessage(result);
            //Toast.makeText(getApplicationContext(),"Finished the http request",Toast.LENGTH_LONG).show();
/*
            try {
                if (connection.getResponseCode() == 200) {
                    startActivity(new Intent(LoginScreen.this, Home.class));
                    //throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed : HTTP error code : ");
            }
*/

        }
    }


}
