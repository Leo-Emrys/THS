package com.example.lmachillot.ths;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewOrdonnanceActivity extends MenuActivity {

    public static long IDORDONNANCE = -1;
    public static String REDIRECTION = "configordo";

    private List<CheckBox> cbtraitements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ordonnance);

        cbtraitements = new ArrayList<>();

        //récupérer traitemens associés
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        List<Traitement> traitements = tdao.getTraitements();
        tdao.close();

        //afficher checkbox traitements
        LinearLayout lignetraitements = (LinearLayout) findViewById(R.id.lignetraitementsordo);

        for(Traitement t : traitements) {
            CheckBox ch = new CheckBox(NewOrdonnanceActivity.this);
            ch.setText(t.getNom());
            //int id = View.generateViewId();
            //ch.setId(id);

            lignetraitements.addView(ch);
            cbtraitements.add(ch);
        }




    }


    public void recharger() {
        Intent intent = new Intent(NewOrdonnanceActivity.this, NewOrdonnanceActivity.class);
        finish();
        startActivity(intent);
    }


    public void creerNewOrdonnance(View view) {

        //recup données générales
        EditText ednom = (EditText) findViewById(R.id.nomnvordo);
        String nom = ednom.getText().toString();

        DatePicker dp = (DatePicker) findViewById(R.id.datenvordo);
        int jour = dp.getDayOfMonth();
        int mois = dp.getMonth();
        int annee = dp.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(annee, mois, jour);
        Date date = cal.getTime();

        EditText edduree = (EditText) findViewById(R.id.dureenvordo);
        int duree = Integer.parseInt(edduree.getText().toString());




        //traitements associés
        List<Long> idtliste = new ArrayList<>();

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();

        for(CheckBox ch : cbtraitements) {
            if(ch.isChecked()) {
                idtliste.add(tdao.getIdTraitementParNom(ch.getText().toString()));
            }
        }

        tdao.close();


        if(idtliste.size()==0) {
            Toast.makeText(this, "Veuillez cocher un traitement !", Toast.LENGTH_SHORT).show();
            recharger();
        } else {

            //table ordonnance
            Ordonnance ordo = new Ordonnance(-1, nom, date, duree);

            IDORDONNANCE = ordo.enregistrerBD(this);



            //ajouter dans TC ordo traitement

            TC_OrdoTraitementDAO otdao = new TC_OrdoTraitementDAO(this);
            otdao.open();

            for(Long idt : idtliste) {
                otdao.ajouterTcTraitementOrdo(idt, IDORDONNANCE);
            }


            // verif DEBUG
            List<String> entrees = otdao.afficherEntrees();
            for(String s : entrees) {
                Log.d("+++++++++++++++++++++++", s);
            }

            //////////////////
            otdao.close();



            //vers rappels

            Intent intent = new Intent(NewOrdonnanceActivity.this, SetRappelsActivity.class);
            intent.putExtra("IDORDONNANCE", IDORDONNANCE);
            intent.putExtra("REDIRECTION", REDIRECTION);
            finish();
            startActivity(intent);

        }











    }
}
