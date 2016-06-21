package com.example.doreen.myapplication;

/**
 * Created by Doreen on 24.05.2016.
 * Anzeige eines einzelnen Note mit allen Merkmalen.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doreen.myapplication.db.TaskDbHelper;

public class DisplayNote extends Activity {

    private TaskDbHelper mydb ;
    public final int PICK_CONTACT = 2015;
    TextView name ;
    TextView description;
    TextView choosedate;
    TextView time;
    RadioButton raNormal;
    RadioButton raHoch;
    RadioGroup radioGroup;
    String setprio;
    String prio;
    String s;
    TextView contactView;
    String orderby = "finished ASC";
    ImageView image_Finished;
    ImageView deleteCon;
    ImageView sendSMS;
    ImageView sendMail;
    TextView showSMS;
    TextView showMail;
    int id_To_Update = 0;
    boolean priori;
    String phonenumber;


    //Erstellung der View und Verbindung aller Elemente mit den Daten.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notes);
        name = (TextView) findViewById(R.id.editTextNote);
        description = (TextView) findViewById(R.id.editTextDescription);
        choosedate = (TextView) findViewById(R.id.editTextDate);
        time = (TextView) findViewById(R.id.editTextTime);
        contactView = (TextView) findViewById(R.id.ContactView);
        raNormal = (RadioButton) findViewById(R.id.radioButtonNormal);
        raHoch = (RadioButton) findViewById(R.id.radioButtonHoch);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        image_Finished = (ImageView) findViewById(R.id.Image_Finished);
        deleteCon = (ImageView) findViewById(R.id.deleteContact);
        sendSMS = (ImageView) findViewById(R.id.btn_sendSMS);
        sendMail = (ImageView) findViewById(R.id.btn_sendMail);
        showMail = (TextView) findViewById(R.id.textViewMail);
        showSMS = (TextView) findViewById(R.id.textViewSMS);

        mydb = new TaskDbHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            //Anzeige, falls es ein bereits bestehender Note ist, aber noch keine Bearbeitung möglich.
            int Value = extras.getInt("id");
            if(Value>0){
                //means this is the view part not the add contact part.
                Cursor rs = mydb.getData(Value, orderby);
                id_To_Update = Value;
                rs.moveToFirst();

                String note = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_NOTE));
                String descri = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_DESCRIPTION));
                String time = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_TIME));
                String date = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_DATE));
                String contact = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_CONTACT));
                prio = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_PRIORITY));
                String mail = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_CONTACT_MAIL));
                String sms = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_CONTACT_PHONE));

                s = rs.getString(rs.getColumnIndex(TaskDbHelper.COLUMN_FINISHED));

                //Es wird ausgelesen, ob das jeweilige Note bereits erledigt wurde. Jeweilige Bild wird gesetzt.
                if (s.equals("false")) {
                    image_Finished.setImageResource(R.drawable.no);
                } else if(s.equals("true")){
                    image_Finished.setImageResource(R.drawable.ok);
                }

                //Listener wird gesetzt, falls der Erledigt-Button gedrückt wird.
                image_Finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(s.equals("false")){
                           image_Finished.setImageResource(R.drawable.ok);
                           mydb.updateFinished(id_To_Update, "true");
                           Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                           intent.putExtra("orderby", "finished ASC");
                           startActivity(intent);
                       }else if(s.equals("true")){
                           image_Finished.setImageResource(R.drawable.no);
                           mydb.updateFinished(id_To_Update, "false");
                           Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                           intent.putExtra("orderby", "finished ASC");
                           startActivity(intent);
                       }
                    }
                });

                //Es wird ausgelesen, ob das jeweilige Note hoch oder normal eingestuft wurde. Jeweilige Bild wird gesetzt.
                if (prio.equals("normal")){
                    priori = true;
                } else if(prio.equals("hoch")){
                    priori = false;
                }
                if (!rs.isClosed())
                {
                    rs.close();
                }

                //Speichern-Button
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                //Note-Name
                name.setText((CharSequence)note);
                name.setFocusable(false);
                name.setClickable(false);

                //Beschreibung
                description.setText((CharSequence)descri);
                description.setFocusable(false);
                description.setClickable(false);

                //Mailadresse und Telefonnummer
                showMail.setText((CharSequence)mail);
                showSMS.setText((CharSequence)sms);

                //Kontaktname
                this.contactView.setText((CharSequence)contact);
                this.contactView.setFocusable(false);
                this.contactView.setClickable(false);

                //Löschknopf für Kontakt
                this.deleteCon.setFocusable(false);
                this.deleteCon.setClickable(false);

                //Datum
                choosedate.setText((CharSequence)date);
                choosedate.setFocusable(false);
                choosedate.setClickable(false);

                //Uhrzeit
                this.time.setText((CharSequence)time);
                this.time.setFocusable(false);
                this.time.setClickable(false);

                //Prioritätauswahl
                if (priori == true){
                    this.raNormal.setChecked(true);
                } else if(priori == false){
                    this.raHoch.setChecked(true);
                }

                this.raNormal.setFocusable(false);
                this.raNormal.setClickable(false);
                this.raHoch.setFocusable(false);
                this.raHoch.setClickable(false);

                //Überprüfung, ob eine Telefonnummer vorhanden ist um SMS zu schreiben.
                if(showSMS.getText().equals("")){
                    sendSMS.setVisibility(View.INVISIBLE);
                    sendSMS.setEnabled(false);
                }else {
                    sendSMS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendSMSMessage();
                        }
                    });
                }

                //Überprüfung, ob eine Internetverbindung vorhanden ist, um eine Email zu schreiben
                if(Connection()) {
                    if(showMail.getText().equals("")){
                        sendMail.setVisibility(View.INVISIBLE);
                        sendMail.setEnabled(false);
                    }else {
                        sendMail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendEmail();
                            }
                        });
                    }
                }else{
                    sendMail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Es besteht keine Internetverbindung.", Toast.LENGTH_SHORT).show();
                        }
                    });
                     }
            }else{
                //Anzeige, falls es ein neuer Note ist. Alle Daten können geändert werden.

                image_Finished.setVisibility(View.INVISIBLE);
                sendMail.setVisibility(View.INVISIBLE);
                sendSMS.setVisibility(View.INVISIBLE);

                //Speichern-Button
                Button b = (Button)findViewById(R.id.button1);

                //Note-Name
                name.setFocusable(true);
                name.setClickable(true);

                //Beschreibung
                description.setFocusable(true);
                description.setClickable(true);

                //Sende-Button für SMS
                sendSMS.setFocusable(true);
                sendSMS.setClickable(true);

                //Sende-Button für Email.
                sendMail.setFocusable(true);
                sendMail.setClickable(true);

                //Auswahl-Möglichkeit eines Kontakts.
                this.contactView.setFocusable(true);
                this.contactView.setClickable(true);
                contactView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(i, PICK_CONTACT);
                    }
                });

                //Löschfunktion für den Kontakt.
                this.deleteCon.setFocusable(true);
                this.deleteCon.setClickable(true);

                //Datum-Auswahl
                choosedate.setFocusable(true);
                choosedate.setClickable(true);
                choosedate.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DateDialog dialog=new DateDialog(v);
                        FragmentTransaction ft =getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                    }
                });

                //Zeit-Auswahl
                this.time.setFocusable(true);
                this.time.setClickable(true);
                time.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TimeDialog dialog = new TimeDialog(v);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "TimePicker");
                    }
                });

                //Priorität-Auswahl
                this.raNormal.setFocusable(true);
                this.raNormal.setClickable(true);
                this.raHoch.setFocusable(true);
                this.raHoch.setClickable(true);

                deleteCon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactView.setText("");
                    }
                });

            }
        }
    }

    // Menüs werden erzeugt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
            int Value = extras.getInt("id");
            if(Value>0){
                //falls Note ausgewählt wurde
                getMenuInflater().inflate(R.menu.display_contact, menu);
            }
            else{
                //falls in Hauptansicht
                getMenuInflater().inflate(R.menu.mainmenu, menu);
            }
        }
        return true;
    }

    //Untermenü wird erzeugt
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            //Kontakt bearbeiten. Alle Datensätze sind änderbar.
            case R.id.Edit_Contact:
                //Speichern-Button
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);

                //Note-Name
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                //Beschreibung
                description.setEnabled(true);
                description.setFocusableInTouchMode(true);
                description.setClickable(true);

                //Löschfunktion des Kontakts
                deleteCon.setEnabled(true);
                deleteCon.setFocusableInTouchMode(true);
                deleteCon.setClickable(true);
                deleteCon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactView.setText("");
                    }
                });

                //Datum-Auswahl
                choosedate.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DateDialog dialog=new DateDialog(v);
                            FragmentTransaction ft =getFragmentManager().beginTransaction();
                            dialog.show(ft, "DatePicker");
                    }
                });

                //Zeit-Auswahl
                time.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TimeDialog dialog = new TimeDialog(v);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "TimePicker");
                    }
                });

                //Kontakt
                contactView.setEnabled(true);
                contactView.setFocusableInTouchMode(true);
                contactView.setClickable(true);
                contactView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(i, PICK_CONTACT);
                    }
                });

                //Prioriät-Auswahl
                raHoch.setEnabled(true);
                raHoch.setFocusableInTouchMode(true);
                raHoch.setClickable(true);

                raNormal.setEnabled(true);
                raNormal.setFocusableInTouchMode(true);
                raNormal.setClickable(true);

                return true;

            //Falls Note gelöscht werden soll.
            case R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Note wird gelöscht
                                mydb.deleteNote(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                intent.putExtra("orderby", "finished ASC");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Falls Nutzer das Löschen abgebrochen hat.
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Sind Sie sicher?");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Email-Sende-Funktion. Alle notwendigen Angaben werden ausgelesen
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {showMail.getText().toString()};


        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Notiz: " + name.getText());
        emailIntent.putExtra(Intent.EXTRA_TEXT, description.getText());

        try {
            startActivity(Intent.createChooser(emailIntent, "Sende Email..."));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Es wurde kein Email-Client installiert.", Toast.LENGTH_SHORT).show();
        }
    }

    //SMS-Sende-Funktion. Alle notwendigen Angaben werden ausgelesen
    protected void sendSMSMessage() {
        String phoneNo = showSMS.getText().toString();
        String message = name.getText() + " - " + description.getText();

        if( phonenumber != ""){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS gesendet.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS konnte nicht gesendet werden.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }else{
            Toast.makeText(getApplicationContext(), "Keine Telefonnummber vorhanden.", Toast.LENGTH_LONG).show();
        }

    }

    //Überprüfung, ob mit Internet verbunden.
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


    //Kontaktliste wird aufgerufen, zur Auswahl eines Kontaktes aus dem Telefonbuch
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int mail = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);

            ContentResolver cr  = getContentResolver();
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String contactAddress   = "";
            Cursor cursorEmail      = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[] {id},
                    null);
            if(cursorEmail.moveToFirst()) {
                contactAddress = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }


            showSMS.setText(cursor.getString(column));
            showMail.setText(contactAddress);
            contactView.setText(cursor.getString(name));
        }
    }

   //Änderungen in der GUI werden in die Datenbank übertragen, je nachdem werden die Daten gelöscht, erstellt oder aktualisiert.
    public void run(View view)
    {
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            int Value = extras.getInt("id");
            if(raHoch.isChecked()){
                setprio = "hoch";
            } else if(raNormal.isChecked()){
                setprio = "normal";
            }
            if(Value>0){
                if(mydb.updateNote(id_To_Update, name.getText().toString(), description.getText().toString(), choosedate.getText().toString(), time.getText().toString(), setprio, "false" , contactView.getText().toString(), showSMS.getText().toString(), showMail.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("orderby", "finished ASC");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(mydb.insertContact(name.getText().toString(), description.getText().toString(), choosedate.getText().toString(), time.getText().toString(), setprio, "false", contactView.getText().toString(), "", "")){
                    Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("orderby", "finished ASC");
                startActivity(intent);
            }
        }
    }
}
