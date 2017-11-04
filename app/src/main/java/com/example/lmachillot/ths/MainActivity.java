package com.example.lmachillot.ths;


import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends MenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();

        boolean traitement = tdao.traitementPresent();

        //long sup = tdao.supprimerTousTraitements();
        // DEBUG
        List<Traitement> listet = tdao.getTraitements();
        //////

        tdao.close();

        //Log.d("Supression traitements", "++++++++++++++ "+sup);




        //Affichage DEBUG
        /*
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo aminfo = am.getNextAlarmClock();
        Calendar cal = Calendar.getInstance();
        Date date = new Date(aminfo.getTriggerTime());
        cal.setTime(date);




        Log.d("+++++++++ proch alarme ", cal.toString());
*/
        Log.d("*********************", listet.size()+" traitement enregistrés");
        for(Traitement t : listet) {
            Log.d("+++++++++ TRAITEMENT ", t.toString());
        }

        ///////////////////////////////////////////

        Log.d("*********************", traitement+" traitement enregistrés");





        if(traitement) {
            Intent intent = new Intent(MainActivity.this, ProchaineDateActivity.class);
            finish();
            startActivity(intent);
        }



        // DEBUG supprimer tables
/*
        DAOBase dao = new TraitementDAO(this);
        SQLiteDatabase mDb = dao.open();
        DatabaseHandler dh = dao.getmHandler();
        dh.supprimerTables(mDb);
*/
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_about:
                intent = new Intent(MainActivity.this, RessourcesActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_ordonnance:
                intent = new Intent(MainActivity.this, ConfigOrdoActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_newtraitement:
                intent = new Intent(MainActivity.this, NewTraitementActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_stocks:
                intent = new Intent(MainActivity.this, SetRappelsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void informations(View view) {
        Intent intent = new Intent(MainActivity.this, RessourcesActivity.class);
        startActivity(intent);
    }

    public void nouveauTraitement(View view) {
        Intent intent = new Intent(MainActivity.this, NewTraitementActivity.class);
        finish();
        startActivity(intent);
    }
}