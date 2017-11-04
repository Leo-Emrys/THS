package com.example.lmachillot.ths;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class ConfigZonesActivity extends MenuActivity {

    public static long IDTRAITEMENT=-1;
    private Traitement traitement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_zones);

        //récupérer variable
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);

        if(IDTRAITEMENT<0) {
            Toast.makeText(this, "pas de traitement associé!", Toast.LENGTH_SHORT).show();
            // redirection ?
            Intent intent2 = new Intent(ConfigZonesActivity.this, MainActivity.class);
            finish();
            startActivity(intent2);
        }

        // recuperer traitement
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        traitement = tdao.getTraitementParId(IDTRAITEMENT);
        tdao.close();

        //completer titre avec nom traitement
        TextView tvtitre = (TextView) findViewById(R.id.titreconfigzones);
        tvtitre.setText(getResources().getString(R.string.titre_configzone)+" "+traitement.getNom());


        //récupérer zones du traitement
        ZoneDAO zdao = new ZoneDAO(this);
        zdao.open();
        List<Zone> zones = zdao.getZonesFrom(IDTRAITEMENT);
        zdao.close();

        //Liste des zones
        String[] columns = new String[] { "_id", "idzone", "zone"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);

        int compteur=1;

        for(Zone z : zones) {
            String nomzone=z.getIntitule()+" "+z.getCote();
            long idzone = z.getId();
            matrixCursor.addRow(new Object[] { compteur, idzone, nomzone });
            compteur++;
        }

        String[] from = new String[] {"idzone", "zone"};

        int[] to = new int[] { R.id.idzone, R.id.nomzone};


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.lignezone, matrixCursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.listzones);
        lv.setAdapter(adapter);



    }

    public void ajouterZone(View view) {
        Intent intent = new Intent(ConfigZonesActivity.this, ZonesActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("REDIRECTION", "configzones");
        finish();
        startActivityForResult(intent, 0);
    }

    public void recharger() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public void supprimerZone(View view) {
        //récupérer id zone
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView tvid = (TextView) parent.getChildAt(0);
        long idzone = Long.parseLong(tvid.getText().toString());

        //supprimer dans table TC
        TC_TraitementZoneDAO tzdao = new TC_TraitementZoneDAO(this);
        tzdao.open();
        long supp = tzdao.deleteTcZoneFromTraitement(idzone, IDTRAITEMENT);
        tzdao.close();

        Log.d("!!!!!!!!!!!!!!!!!!!!!!!", "SUPPRIME "+supp);

        recharger();

    }
}
