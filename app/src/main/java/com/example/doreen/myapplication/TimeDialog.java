package com.example.doreen.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Doreen on 06.06.2016.
 * TimeDialog wird erzeugt um eine Auswahl der Zieluhrzeit zu erleichtern.
 */
@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
   TextView txt_time;
    String sminute;
    String shourOfDay;
    public TimeDialog(View view){
        txt_time =(TextView)view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, false);

    }



    //Nutze die ausgewählte Zeit, bringe sie in die geeignete Form und zeige diese an.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(minute < 10 ){
            sminute = String.format("%02d", minute);
        }else{
            sminute =  String.valueOf(minute);
        }

        if(hourOfDay < 10 ){
            shourOfDay = String.format("%02d", hourOfDay);
        }else{
            shourOfDay =  String.valueOf(hourOfDay);
        }
        txt_time.setText(shourOfDay+":"+sminute);
    }
}