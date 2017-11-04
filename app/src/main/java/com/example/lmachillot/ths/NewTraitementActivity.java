package com.example.lmachillot.ths;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewTraitementActivity extends MenuActivity {

    public static long IDTRAITEMENT=-1;

    Spinner listehormones;
    Spinner listetypes;


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Êtes-vous sûr-e de vouloir annuler ?");

        final Context context = this;
        // set dialog message
        alert.setMessage("Le traitement ne sera pas créé.")
                .setCancelable(true)
                .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent(NewTraitementActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alert.create();

        // show it
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_traitement);

        // Spinner hormones :

        listehormones = (Spinner) findViewById(R.id.listehormones);

        List<String> liste = new ArrayList<String>();
        for (Hormone h : Hormone.values()) {
            liste.add(h.name());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                liste
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listehormones.setAdapter(adapter);




            //Spinner type
            listetypes = (Spinner) findViewById(R.id.type);

            List<String> types = new ArrayList<String>();

            for (Type t : Type.values()) {
                types.add(t.name());
            }

            ArrayAdapter adapter4 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listetypes.setAdapter(adapter4);


    }


    public void recharger() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }



    public void enregistrerTraitement(View view) {

        //récupérer données
        boolean ok = true;

        EditText ctnom = (EditText) findViewById(R.id.nomtraitement);
        String nomtraitement = ctnom.getText().toString().trim();

        //vérifier qu'il n'existe pas d'autre traitement de ce nom
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        long idexistant = tdao.getIdTraitementParNom(nomtraitement.trim());
        tdao.close();

        Log.d("id ??????????????????", idexistant+"");

        if(nomtraitement.length()<=0) {
            Toast.makeText(this, "Entrez un nom",  Toast.LENGTH_SHORT).show();
            ok=false;
        }

        else if(idexistant!=-1) {
            Toast.makeText(this, "Ce traitement existe déjà. Entrer un nom différent",  Toast.LENGTH_SHORT).show();
            ok=false;
        }

        Spinner cthormone = (Spinner) findViewById(R.id.listehormones);
        String hormonestr = cthormone.getSelectedItem().toString();
        Hormone hormone = null;
        for(Hormone h : Hormone.values()) {
            if(h.name().equals(hormonestr)) {
                hormone=h;
            }
        }

        int frequence;
        EditText ctfrequence = (EditText) findViewById(R.id.freqtraitement);
        try {
            frequence = Integer.valueOf(ctfrequence.getText().toString());
        } catch (Exception e) {
            frequence=-1;
            ok=false;
        }

        if(frequence<=0) {
            Log.d("Frequence", "----------------------------------");
            Toast.makeText(this, "Entrez une fréquence (nombre positif)",  Toast.LENGTH_SHORT).show();
            ok=false;
        }


        Spinner cttype = (Spinner) findViewById(R.id.type);
        String typestr = cttype.getSelectedItem().toString();
        Type type = null;
        for(Type t : Type.values()) {
            if(t.name().equals(typestr)) {
                type=t;
            }
        }


        EditText ctdosage = (EditText) findViewById(R.id.dosage);
        String dosage = ctdosage.getText().toString();

        if(dosage.length()<=0) {
            Toast.makeText(this, "Entrez un dosage",  Toast.LENGTH_SHORT).show();
            ok=false;
        }


        if(ok) {
            //créer objet Traitement

            Traitement t = new Traitement(-1, nomtraitement, hormone, frequence, null, null, null, dosage, type);


            Log.d("Traitement :::::::::: ", t.toString());

            // enregistrer dans BD
            tdao.open();
            tdao.ajouterTraitement(t);

            Log.d("id traitement maj ?", t.getId()+"");

            IDTRAITEMENT=t.getId();

            List<Traitement> traitements = tdao.getTraitements();

            for(Traitement t1 : traitements) {
                Log.d("ENREGISTRE ````````", t1.toString());
            }


            tdao.close();


            Intent intent = new Intent(NewTraitementActivity.this, ZonesActivity.class);
            finish();
            intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
            intent.putExtra("REDIRECTION", "premiereprise");
            startActivityForResult(intent, 0);


        } else {
            recharger();
        }



    }
}
