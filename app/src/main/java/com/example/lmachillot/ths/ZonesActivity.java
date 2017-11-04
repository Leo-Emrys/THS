package com.example.lmachillot.ths;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class ZonesActivity extends MenuActivity {

    public static long IDTRAITEMENT=-1;
    private static String REDIRECTION = "configzones";

    Spinner zones;
    Spinner cote;
    Spinner coteedit;

    @Override
    public void onBackPressed() {
        Log.d("+++++++++++ Redirection", REDIRECTION);
        if(REDIRECTION.equals("premiereprise")) {
            Log.d("???????????????????", "ok");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Êtes-vous sûr-e de vouloir annuler ?");

            final Context context = this;
            // set dialog message
            alert.setMessage("Les données du nouveau traitement seront effacées.")
                    .setCancelable(true)
                    .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            //supprimer ligne traitement incomplet
                            TraitementDAO tdao = new TraitementDAO(context);
                            tdao.open();
                            tdao.supprimerTraitement(IDTRAITEMENT);
                            tdao.close();
                            //retour accueil
                            Intent intent = new Intent(ZonesActivity.this, MainActivity.class);
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

            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
        else if(REDIRECTION.equals("configzones")) {
            Intent intent = new Intent(ZonesActivity.this, ConfigZonesActivity.class);
            intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
            finish();
            startActivityForResult(intent, 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zones);


        REDIRECTION="premiereprise";

        //récupérer variables
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);

        Log.d("+++++++++++++++++", "ID TRAITEMENT ZONES ACTIVITY + "+ IDTRAITEMENT);


        if(IDTRAITEMENT<0) {
            Toast.makeText(this, "pas de traitement associé!", Toast.LENGTH_SHORT).show();
            // redirection
            Intent intent2 = new Intent(ZonesActivity.this, MainActivity.class);
            finish();
            startActivity(intent2);
        }

        String var_redir=intent.getStringExtra("REDIRECTION");
        Log.d("******* VAR REDIRECTION", var_redir+"....");
        if(var_redir!=null && var_redir!="") {
            REDIRECTION=var_redir;
        }

        //récup zones dans BD
        ZoneDAO zdao = new ZoneDAO(this);
        zdao.open();
        List<String> listezones = zdao.getIntitulesZones();

        zdao.close();

        listezones.add(0, "(aucune)");

        //lier au Spinner
        zones = (Spinner) findViewById(R.id.zoneselected);

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listezones
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zones.setAdapter(adapter);

        //init défaut
        zones.setSelection(0);


        cote = (Spinner) findViewById(R.id.coteselected);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.coté));
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cote.setAdapter(adapter2);


        coteedit = (Spinner) findViewById(R.id.newcote);
        ArrayAdapter adapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.coté));
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coteedit.setAdapter(adapter3);




    }

    public void passer(View view) {
        Intent intent=getIntent();
        if(REDIRECTION.equals("premiereprise")) {
            intent = new Intent(ZonesActivity.this, PremierePriseActivity.class);
        } else if(REDIRECTION.equals("configzones")) {
            intent = new Intent(ZonesActivity.this, ConfigZonesActivity.class);
        }
        finish();
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        startActivityForResult(intent, 0);


    }


    public void recharger() {
        Intent intent = new Intent(ZonesActivity.this, ZonesActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("REDIRECTION", REDIRECTION);
        finish();
        startActivity(intent);
    }


    public void validerEtContinuer(View view) {

        insererDonnees();

        recharger();

    }


    public void validerTerminer(View view) {

        if(insererDonnees())
            passer(view);
        else {
            recharger();
        }
    }


    public boolean insererDonnees() {

        //Selections

        String zone;
        String cote;
        Zone z = null;

        Spinner ctzone = (Spinner) findViewById(R.id.zoneselected);
        zone = ctzone.getSelectedItem().toString();

        //Log.d("______________________zone select : ", zone);

        if(zone.equals("(aucune)")) { // pas de selection -> regarder la partie edit

            EditText ctnewzone = (EditText) findViewById(R.id.newpartiecorps);
            zone=ctnewzone.getText().toString();

            if((zone.length()==0)) { // aucune entrée alors que validé ??
                Toast.makeText(this, "données manquantes\n selectionnez une zone ou appuyez sur \"ne pas ajouter de zone\" ", Toast.LENGTH_SHORT).show();
                return false;

            } else { // sinon récupérer aussi cote

                Spinner ctcote = (Spinner) findViewById(R.id.newcote);
                cote = ctcote.getSelectedItem().toString();

                Log.d("_________cote select : ", "_____________"+cote);
            }

            Zone newzone = new Zone(-1, zone, cote);
            ZoneDAO zdao = new ZoneDAO(this);
            zdao.open();
            zdao.ajouterZone(newzone);
            zdao.close();

            z=newzone;

            Log.d("id entré_____________", "__________________"+z.getId());

        } else { // si selection -> recup coté selectionné

            Spinner ctcote = (Spinner) findViewById(R.id.coteselected);
            cote = ctcote.getSelectedItem().toString();

            Log.d("______cote select : ", "________________"+cote);

            // retrouver zone correspondante
            ZoneDAO zdao = new ZoneDAO(this);
            zdao.open();
            List<Zone> listzones = zdao.getZones();
            zdao.close();


            for(Zone z1 : listzones) {
                if(z1.getIntitule().equals(zone)) {
                    if(z1.getCote().substring(0, 4).equals(cote.substring(0, 4))) {
                        z=z1;
                    }
                }
            }

            if(z==null) {
                Toast.makeText(this, "zone invalide", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        //insérer lien traitement -- zone dans table tc_traitement_zone

        TC_TraitementZoneDAO tzdao = new TC_TraitementZoneDAO(this);
        tzdao.open();
        long idtz = tzdao.ajouterTraitementZone(z.getId(), IDTRAITEMENT, 0);

        if(idtz==-1) {
            Toast.makeText(this, "zone déjà enregistrée pour ce traitement", Toast.LENGTH_SHORT).show();
            return false;
        }
        //affichage + debug

        List<TcTraitementZone> entrees = tzdao.getTraitementZones();

        tzdao.close();

        for(TcTraitementZone tz : entrees) {
            Log.d("entrée ||||||||||", "||||||||||||||||||||||| "+tz.toString());
        }

        return true;
    }

}
