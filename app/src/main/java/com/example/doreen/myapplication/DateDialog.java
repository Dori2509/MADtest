package com.example.doreen.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Doreen on 06.06.2016.
 * DateDialog wird erzeugt um eine Auswahl des Zieldatums zu erleichtern.
 */
@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView txtdate;
    public DateDialog(View view){
        txtdate=(TextView)view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    //Nutze das ausgewählte Datum, bringe es in die geeignete Form und zeige diese an.
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String sday;
        if(day < 10 ){
            sday = String.format("%02d", day);
        }else {
            sday = String.valueOf(day);
        }

        String smonth;
        month = month+1;
        if(month < 10 ){
            smonth = String.format("%02d", month);
        }else {
            smonth = String.valueOf(month);
        }

        String date=sday+"-"+(smonth)+"-"+year;
        txtdate.setText(date);
    }



}