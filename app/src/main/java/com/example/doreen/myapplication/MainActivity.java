package com.example.doreen.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.doreen.myapplication.db.MySimpleArrayAdapter;
import com.example.doreen.myapplication.db.TaskDbHelper;

import java.util.ArrayList;

//Listenübersicht wird erzeugt.
public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    TaskDbHelper mydb;
    public int id_To_Search;
    public  ArrayList id_list;
    public String orderby = "finished ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sortierung wird ausgelesen und erstellt.
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("orderby").equals("priority ASC, date DESC"))
        {
            orderby= "priority ASC, date DESC";
        } else if(bundle.getString("orderby").equals("date DESC, priority ASC"))
        {
            orderby= "date DESC, priority ASC";
        }else{
            orderby = "finished ASC";
        }


        //Daten für die Listenübersicht werden ausgelesen.
        mydb = new TaskDbHelper(this);
        ArrayList array_list = mydb.getAllNotes(orderby);
        ArrayList descr_list = mydb.getAllPriority(orderby);
        ArrayList date_list = mydb.getAllDate(orderby);
        ArrayList finished_list = mydb.getAllFinished(orderby);
        id_list = mydb.getAllID(orderby);

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, array_list, descr_list, date_list, finished_list, mydb, id_list);

        //Mit Hilfe des Adapters werden, die Daten dementsprechend angezeigt.
        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(adapter);
        obj.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                id_To_Search = (int) id_list.get(arg2);
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                Intent intent = new Intent(getApplicationContext(),DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);

            }
        });

    }

    //Die obere Menüleiste wird hinzugefügt.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    //Funktionen zum Menü werden hinzugefügt: Neues Note, Sortierung
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.action_add_task:Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(),DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            case R.id.action_sortbyDown:
                orderby = "priority ASC, date DESC";
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.putExtra("orderby", orderby);
                startActivity(refresh);//Start the same Activity
                finish(); //finish Activity.
                return true;
            case R.id.action_sortbyUp:
                orderby = "date DESC, priority ASC";
                Intent refresh2 = new Intent(this, MainActivity.class);
                refresh2.putExtra("orderby", orderby);
                startActivity(refresh2);//Start the same Activity
                finish(); //finish Activity.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}