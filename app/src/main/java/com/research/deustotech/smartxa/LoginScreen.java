package com.research.deustotech.smartxa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
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
    //public static String patientsName;
    //public static String doctorsName;

    Button loginButton;
    RequestTokenTask request;

    TextView patientName;
    TextView doctorName;
    Spinner stage;
    SharedPreferences sp;

    // Stuff to reference the textview on the custom input_dialog layout
    //LayoutInflater layoutInflater;
    //View promptView;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        loginButton = (Button) findViewById(R.id.authbutton);

        sp = getSharedPreferences( "Smartxa_Preferences", Context.MODE_PRIVATE);

        // Create an instance of the async task -- not executed yet
        //request = new RequestTokenTask();
        //request.execute();

        //layoutInflater = LayoutInflater.from(LoginScreen.this);
        //promptView = layoutInflater.inflate(R.layout.input_dialog, null);

        //editText = (EditText) promptView.findViewById(R.id.edittext);


    }


    /*
    public void showPasswordListener()
    {
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    //checked
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                //not checked
            }

        });

    }

*/

    public void loginButtonClicked(View v)
    {
         //startActivity(new Intent(LoginScreen.this, UserProfile.class));

        // Reference the textviews and spinner by Id
        patientName = (TextView) findViewById(R.id.patientNameText);
        doctorName = (TextView) findViewById(R.id.doctorNameText);
        stage = (Spinner) findViewById(R.id.spinner2);

        // Show warning dialogs if the patient or doctor name text views are empty
        if (patientName.getText().toString().isEmpty())
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()){
                        new AlertDialog.Builder(LoginScreen.this)
                                .setTitle("Invalid Information")
                                .setMessage("Please enter the name of the patient.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        patientName.setHintTextColor(Color.RED);
                                    }
                                }).show();
                    }
                }
            });

        }
        else if (doctorName.getText().toString().isEmpty())
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()){
                        new AlertDialog.Builder(LoginScreen.this)
                                .setTitle("Invalid Information")
                                .setMessage("Please enter the name of the doctor.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        doctorName.setHintTextColor(Color.RED);
                                    }
                                }).show();
                    }
                }
            });
        }
        else
        {
            // If valid info is entered, prompt the user for their password

            try
            {

                username = doctorName.getText().toString();
                showInputDialog();


                // Store the user entered data in publically accessible vars

                MenuFragment.setPatientsName(patientName.getText().toString());
                MenuFragment.setDoctorsName(doctorName.getText().toString());
                MenuFragment.setPatientStage(stage.getSelectedItem().toString());

                // Update the shared preferences
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("patient_name",patientName.getText().toString());
                editor.putString("doctor_name",doctorName.getText().toString());
                editor.putString("stage",stage.getSelectedItem().toString());


            } catch (Exception e) {
                System.out.println("Failed" + e);
                // log error. failed to communicate with api: + e
            }
        }


    }



    // Display a dialog to prompt the user (doctor) for their password.
    // Make a request to the api to validate the credentials
    // If it is successful, load the next screen
    protected void showInputDialog() {


        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreen.this);
        alertDialogBuilder.setView(promptView);


        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

/*

        CheckBox showPass = (CheckBox) findViewById(R.id.showPasswordCheckBox);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    //checked
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                //not checked
            }

        });

*/

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        password = editText.getText().toString();
                        MenuFragment.setUserPassword(password);

                        startActivity(new Intent(LoginScreen.this, UserProfile.class));
                        //sendCommand(username, password);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


        CheckBox showPass = (CheckBox) alert.findViewById(R.id.showPasswordCheckBox);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    //checked
                    System.out.println("Checked");
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    System.out.println("Unchecked");
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                //not checked
            }

        });
    }



    // This method sends an http request to the api. It receives a token response
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
                    json.put("username", _username.toString());// "carlos"
                    json.put("password",_password.toString());//"numero1234"
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

                    // Check the response code.
                    if (respCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        in = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null) {
                            reply += line;
                        }
                        // Clean up by closing the reader and input stream
                        reader.close();
                        in.close();

                        // Debug: display the response in console
                        System.out.println("\nresponse content " + reply);

                        // In our case, the API returns an OK response even if the username or password
                        // are invalid. So we Check the response content.
                        if (!reply.toString().contains("Usuario inválido"))
                        {
                            // Open the next window on a successful login
                            startActivity(new Intent(LoginScreen.this, UserProfile.class));
                        }
                        else
                        {
                            // If the username (doctor's name) or password are invalid, display an alert.

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (!isFinishing()){
                                        new AlertDialog.Builder(LoginScreen.this)
                                                .setTitle("Invalid Information")
                                                .setMessage("The doctor's name or the password you entered were invalid.")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Whatever...
                                                        //doctorName.setHintTextColor(Color.RED);
                                                    }
                                                }).show();
                                    }
                                }
                            });
                        }
                        // If the password is invalid, diplay an alert dialog to notify the user
                        /*
                        else if ( reply == "")
                        {

                        }
                        */

                    } else {
                        // If the app cannot communicate with the server, then it will fall through to here.
                        Toast.makeText(getApplicationContext(),"Failed login attempt. Please try again", Toast.LENGTH_LONG).show();
                        //tv.setText("Failed to connect: " + respCode);
                    }

                    // Disconnect after the request.
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }





/* Deprecated code */

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
