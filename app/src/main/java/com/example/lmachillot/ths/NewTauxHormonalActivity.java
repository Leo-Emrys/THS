package com.example.lmachillot.ths;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewTauxHormonalActivity extends MenuActivity {

    Spinner listehormones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_taux_hormonal);

        listehormones = (Spinner) findViewById(R.id.listehormonesnewtaux);

        List<String> liste = new ArrayList<String>();
        for (Hormone h : Hormone.values()) {
            if(!h.name().equals("anti_androgènes"))
            liste.add(h.name());
        }

        //todo : récupérer hormones ajoutées précédemments ? (table taux hormonaux)
        TauxHormonalDAO thdao = new TauxHormonalDAO(this);
        thdao.open();
        List<String> hormonespresentes = thdao.getHormonesConcernees();
        thdao.close();

        if(hormonespresentes!=null && hormonespresentes.size()>0) {
            for(String h : hormonespresentes) {
                if(!Hormone.contains(h)) {
                    liste.add(h);
                }
            }
        }

        liste.add(getResources().getString(R.string.autretexte));

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                liste
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listehormones.setAdapter(adapter);

        //écouter click sur autre pour ajout
        listehormones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(parentView!=null) {
                    EditText ed = (EditText) findViewById(R.id.ajouterhormone);
                    if(listehormones.getSelectedItem().equals(getResources().getString(R.string.autretexte))) {
                        ed.setVisibility(View.VISIBLE);
                    } else {
                        ed.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewTauxHormonalActivity.this, SuiviHormonalActivity.class);
        finish();
        startActivity(intent);
    }

    public void enregistrerDosage(View view) {

        Log.e("5555555555555555555", "enregistrer");

        String hormone = listehormones.getSelectedItem().toString();
        if(hormone.equals(getResources().getString(R.string.autretexte))) { // nouvelle entrée
            EditText hormoneed = (EditText) findViewById(R.id.ajouterhormone);
            hormone = hormoneed.getText().toString();
        }

        DatePicker dp = (DatePicker) findViewById(R.id.dateanalyse);
        int year = dp.getYear();
        int mois = dp.getMonth();
        int jour = dp.getDayOfMonth();

        Calendar cal = Calendar.getInstance();
        cal.set(year, mois, jour);
        Date date = cal.getTime();

        EditText edtaux = (EditText) findViewById(R.id.newtaux);
        double taux = Double.valueOf(edtaux.getText().toString());

        TauxHormonalDAO thdao = new TauxHormonalDAO(this);
        thdao.open();
        long id = thdao.ajouterTaux(new TauxHormonal(-1, hormone, taux, date));
        thdao.close();

        Intent intent = new Intent(NewTauxHormonalActivity.this, SuiviHormonalActivity.class);
        finish();
        startActivity(intent);
    }
}
