package com.example.doreen.myapplication;

/**
 * Created by Doreen on 11.06.2016.
 * Login-Übersicht wird angezeigt.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class startActivity extends Activity {

    public EditText mailadress;
    public EditText password;
    public Button lgn_button;
    public TextView showError;
    public boolean mailInsert = false;
    public boolean passwordInsert = false;

    //Anzeige des Startbildschirms
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startdisplay);

        mailadress = (EditText) findViewById(R.id.mailadress);
        password = (EditText) findViewById(R.id.password);
        lgn_button = (Button) findViewById(R.id.loginbutton);
        showError = (TextView) findViewById(R.id.showError);

        //Login-Button
        lgn_button.setEnabled(false);

        //Editfeld für Email
        mailadress.addTextChangedListener(new TextWatcher() {

            //Falls der Text geändert wird, wird überprüft ob bereits ein Passwort eingegeben wurde, um den Login-Button freizuschalten
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if(mailadress.getText().toString().length()  > 0){
                    mailInsert = true;
                    if(passwordInsert == true){
                        lgn_button.setEnabled(true);
                    }
                }else{
                    mailInsert = false;
                    lgn_button.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        //Passwort, numerisch, 6 Zeichen
        password.addTextChangedListener(new TextWatcher() {
            //Falls der Text geändert wird, wird überprüft ob bereits eine Email eingegeben wurde, um den Login-Button freizuschalten
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if(password.getText().toString().length()  > 0){
                   passwordInsert = true;
                    if(mailInsert == true){
                        lgn_button.setEnabled(true);
                    }
                } else{
                    passwordInsert = false;
                    lgn_button.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        //wenn in das Passwort-Feld geklickt wird, wird dieses leer
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
                showError.setText("");
            }
        });

        //wenn in das Mail-Feld geklickt wird, wird dieses leer
        mailadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailadress.setText("");
                showError.setText("");
            }
        });

        //Internetverbindung wird überprüft, je nachdem werden die Eingaben auf ihre Richtigkeit (im AsyncTask) geprüft oder es wird direkt die Liste angezeigt
        if(Connection()){
            lgn_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mailInput = mailadress.getText().toString();
                    String passwordInput = password.getText().toString();

                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute(mailInput, passwordInput);

                    }
            });

        }else{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("orderby", "finished ASC");
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    //Internetverbindung wird geprüft.
    private boolean Connection() {
        boolean Wifi = false;
        boolean Mobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo NI : netInfo) {
            if (NI.getTypeName().equalsIgnoreCase("WIFI")){
                if (NI.isConnected()){
                    Wifi = true;
                }
            }
            if (NI.getTypeName().equalsIgnoreCase("MOBILE"))
                if (NI.isConnected()){
                    Mobile = true;
                }
        }
        return Wifi || Mobile;
    }

    //Überprüfung der Eingaben im AsyncTask
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String proof;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            String mailproof = params[0];
            String passwordproof = params[1];

            //Zeitpuffer
            try {
                int time = Integer.parseInt("2")*1000;
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Überprüfung des Passworts
            String regex = "^\\d+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(passwordproof);
            boolean passwordNumeric = matcher.find();


            if(passwordNumeric && passwordproof.length() == 6 && isEmailValid(mailproof)){
                //TODO hier muss Webanwendung starten plus TODOliste aufrufen

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("orderby", "finished ASC");
                startActivity(intent);

            //Textliche Anzeige, wenn Eingaben fehlerhaft
            } else if (passwordNumeric == false){
                publishProgress("Sie haben leider falsche Zeichen eingesetzt");
            } else if (passwordproof.length() != 6){
                publishProgress("Das Passwort muss genau 6 Zeichen haben.");
            } else if (isEmailValid(mailproof) == false){
                publishProgress("Sie haben keine Emailadresse eingegeben");
            }
            return proof;
        }

        /*
        This method is called after doInBackground method completes processing. Result from doInBackground is passed to this method.
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
        }

        /*
        This method contains the code which is executed before the background processing starts.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(startActivity.this,
                    "DoMyNote",
                    "Bitten warten...");
        }

        /*
        This method receives progress updates from doInBackground method, which is published via
        publishProgress method, and this method can use this progress update to update the UI thread
         */
        @Override
        protected void onProgressUpdate(String... text) {
            showError.setText(text[0]);

        }

        //Überprüfung, ob Emailadresse im Mail-Format
        public boolean isEmailValid(String email) {
            boolean isValid = false;

            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            CharSequence inputStr = email;

            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                isValid = true;
            }
            return isValid;
        }
    }
}




