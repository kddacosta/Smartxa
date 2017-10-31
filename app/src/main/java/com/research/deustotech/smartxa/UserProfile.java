package com.research.deustotech.smartxa;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;


public class UserProfile extends AppCompatActivity {

    public static String username;
    public static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }




    void setUsername (String user) {
        username = user;
    }

    String getUsername() {
        return username;
    }

    void setPassword (String pass) {
        password = pass;
    }

    private class RequestTokenTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
           try {
               URL url = new URL("http://10.32.8.79/sesion/api/login");
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               connection.setRequestMethod("POST");
               connection.addRequestProperty("username","");
               connection.addRequestProperty("password","");
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
        }
    }
}
