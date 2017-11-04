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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PremierePriseActivity extends MenuActivity {

    public static long IDTRAITEMENT;
    public static String REDIRECTION="prochainedate";
    public static long CREATION = 1;

    Spinner zones;

    boolean zonesselect;


    Calendar cal;

    @Override
    public void onBackPressed() {
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
                            Intent intent = new Intent(PremierePriseActivity.this, MainActivity.class);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiere_prise);

        //récupérer variable idtraitement
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", ZonesActivity.IDTRAITEMENT);
        Log.d("idtraitement //////////", "."+IDTRAITEMENT+".");

        if(IDTRAITEMENT<0) {
            Toast.makeText(this, "pas de traitement associé!", Toast.LENGTH_SHORT).show();
            // redirection ?
            Intent intent2 = new Intent(PremierePriseActivity.this, NewTraitementActivity.class);
            finish();
            startActivity(intent2);
        }

        //init calendrier
        cal = Calendar.getInstance();

        //récup zones dans BD  !! seulement zones associées au traitement concerné
        ZoneDAO zdao = new ZoneDAO(this);
        zdao.open();
        List<Zone> zonesfrom = zdao.getZonesFrom(IDTRAITEMENT);

        zdao.close();


        //verif si liste est vide ou non
        zones = (Spinner) findViewById(R.id.zonePremPrise);


        if(zonesfrom.size()<=0) {
            zonesselect=false;
            zones.setVisibility(View.GONE);
            TextView titrezone = (TextView) findViewById(R.id.titreZonePremPrise);
            titrezone.setText("");
        } else {
            zonesselect=true;
            //Spinner zones
            List<String> listezones = new ArrayList<>();

            for(Zone z : zonesfrom) {
                    listezones.add(z.getIntitule()+" "+z.getCote());

            }


            //lier au Spinner

            ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listezones);

            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            zones.setAdapter(adapter1);

            //init défaut
            zones.setSelection(0);
        }

    }

    public Date recupDate() {
        DatePicker dp = (DatePicker) findViewById(R.id.premdate);
        int jour = dp.getDayOfMonth();
        int mois = dp.getMonth();
        int annee = dp.getYear();

        cal.set(annee, mois, jour);
        Date premdate = cal.getTime();

        return premdate;
    }

    public Traitement majTraitement(Date date) {
        //mettre à jour dates dans Table Traitement :
        //récupérer traitement concerné
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        Traitement traitement = tdao.getTraitementParId(IDTRAITEMENT);
        //maj première date + calcul nouvelle date
        traitement.setPremiereprise(date);
        Calendar cal2 = Calendar.getInstance();
        Date today = cal2.getTime();

        Log.d("!!!!!!!!!!!!!!!!!!!", "avant ? : "+today.after(date));

        if(today.after(date)) {
            traitement.calculeDateRenouv();
            // cas date renouvellement toujours dans le passé
            while(traitement.getDate_renouvellement().before(today)) {
                cal.setTime(traitement.getDate_renouvellement());
                cal.add(Calendar.DATE, traitement.getFrequence());
                traitement.setDate_renouvellement(cal.getTime());
            }
        }
        else {
            traitement.setDate_renouvellement(date);
        }


        //maj BD
        tdao.setDatePremierePrise(traitement);
        tdao.majDateRenouvellement(traitement);
        tdao.close();

        return traitement;
    }

    public Zone getZoneSelect() {

        //récupérer zone selectionnée
        Spinner spzone = (Spinner) findViewById(R.id.zonePremPrise);
        String zonecomp = spzone.getSelectedItem().toString();

        Log.d("zone _____ _______", zonecomp);


        String[] spl = zonecomp.split(" ");
        Log.d("split****************", spl[0]+", ,"+spl[1]);
        String zonestr = spl[0];
        String cotestr = spl[1];

        // retrouver zone correspondante
        ZoneDAO zdao = new ZoneDAO(this);
        zdao.open();
        List<Zone> listzones = zdao.getZones();
        zdao.close();

        Zone zone = null;


        for(Zone z : listzones) {
            if(z.getIntitule().equals(zonestr)) {
                if(z.getCote().substring(0, 4).equals(cotestr.substring(0, 4))) {
                    zone=z;
                }
            }
        }

        if(zone==null) {
            Log.d("-------------------", "zone non trouvée ??");
        }

        return zone;
    }


    public void enregistrerPremDate(View view) {

        Date date = recupDate();

        Traitement traitement = majTraitement(date);

        Zone zone = null;
        Spinner spzone = (Spinner) findViewById(R.id.zonePremPrise);

        Log.d("spzone ?????????????", spzone.toString());

        if(zonesselect) {
            zone = getZoneSelect();
        }

        //créer objet Prise
        Prise prise = new Prise(-1, date, traitement, zone);

        //Enregistrer Prise dans BD
        prise.enregistrerBD(this);





        //**************************Affichage pour verif*******************************

        TraitementDAO tdao=new TraitementDAO(this);
        tdao.open();
        List<Traitement> listet = tdao.getTraitements();
        tdao.close();

        for(Traitement t : listet) {
            Log.d("traitement maj", "---------------- "+t.toString());
        }


        //affichage prises verif
        PriseDAO priseDAO = new PriseDAO(this);
        priseDAO.open();
        List<String> liste = priseDAO.getPrisesStr();
        for(String s : liste) {
            Log.d("PRISES ENREGISTRÉES::::", s);
        }

        priseDAO.close();


        // PASSAGE À ACTIVITÉ SUIVANTE --> RAPPELS

        Intent intent = new Intent(PremierePriseActivity.this, SetRappelsActivity.class);
        finish();
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("REDIRECTION", REDIRECTION);
        intent.putExtra("CREATION", CREATION);
        startActivityForResult(intent, 0);
    }
}
