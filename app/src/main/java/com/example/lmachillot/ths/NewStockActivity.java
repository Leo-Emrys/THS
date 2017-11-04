package com.example.lmachillot.ths;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

public class NewStockActivity extends MenuActivity {

    public static long IDSTOCK = -1;
    public static String REDIRECTION = "configstock";

    private List<CheckBox> cbtraitements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        cbtraitements = new ArrayList<>();

        //récupérer traitemens associés
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        List<Traitement> traitements = tdao.getTraitements();
        tdao.close();

        //afficher checkbox traitements
        LinearLayout lignetraitements = (LinearLayout) findViewById(R.id.lignetraitementstock);

        for (Traitement t : traitements) {
            CheckBox ch = new CheckBox(NewStockActivity.this);
            ch.setText(t.getNom());
            //int id = View.generateViewId();
            //ch.setId(id);

            lignetraitements.addView(ch);
            cbtraitements.add(ch);
        }


    }

    public void recharger() {
        Intent intent = new Intent(NewStockActivity.this, NewStockActivity.class);
        finish();
        startActivity(intent);
    }

    public void creerNewStock(View view) {
        //recup date et durée

        DatePicker dp = (DatePicker) findViewById(R.id.datenvstock);
        int jour = dp.getDayOfMonth();
        int mois = dp.getMonth();
        int annee = dp.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(annee, mois, jour);
        Date date = cal.getTime();

        EditText edduree = (EditText) findViewById(R.id.dureenvstock);
        int duree = Integer.parseInt(edduree.getText().toString());


        //traitements associés
        List<Long> idtliste = new ArrayList<>();

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();

        for (CheckBox ch : cbtraitements) {
            if (ch.isChecked()) {
                idtliste.add(tdao.getIdTraitementParNom(ch.getText().toString()));
            }
        }

        tdao.close();


        if (idtliste.size() == 0) {
            Toast.makeText(this, "Veuillez cocher un traitement !", Toast.LENGTH_SHORT).show();
            recharger();
        } else {

            //table ostock
            Stock stock = new Stock(-1, date, duree);

            IDSTOCK = stock.enregistrerBD(this);


            //ajouter dans TC stock traitement

            TC_StockTraitementDAO stdao = new TC_StockTraitementDAO(this);
            stdao.open();

            for (Long idt : idtliste) {
                stdao.ajouterTcTraitementStock(idt, IDSTOCK);
            }


            // verif DEBUG
            List<String> entrees = stdao.afficherEntrees();
            for (String s : entrees) {
                Log.d("+++++++++++++++++++++++", s);
            }

            //////////////////
            stdao.close();


            //vers rappels

            Intent intent = new Intent(NewStockActivity.this, SetRappelsActivity.class);
            intent.putExtra("IDSTOCK", IDSTOCK);
            intent.putExtra("REDIRECTION", REDIRECTION);
            finish();
            startActivity(intent);
        }

    }

}
