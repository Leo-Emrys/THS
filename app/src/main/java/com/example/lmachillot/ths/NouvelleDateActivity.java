package com.example.lmachillot.ths;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NouvelleDateActivity extends MenuActivity {

    public static long IDTRAITEMENT = -1;

    Traitement traitement;
    private Spinner zones;
    List<Zone> listezones;
    boolean zonesassoc=true;

    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_date);

        //récupérer variable
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);
        if(IDTRAITEMENT<0) {
            Toast.makeText(this, "pas de traitement associé!", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(NouvelleDateActivity.this, ProchaineDateActivity.class);
            finish();
            startActivity(intent2);
        }

        cal = Calendar.getInstance();

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        traitement = tdao.getTraitementParId(IDTRAITEMENT);
        tdao.close();




        //récupérer données dernière prise
        PriseDAO pdao = new PriseDAO(this);
        pdao.open();
        Prise derniereprise = pdao.dernierePriseFrom(traitement);
        pdao.close();

        //affichage
        //nom traitement
        TextView tvtraitement = (TextView) findViewById(R.id.traitementnvdate);
        tvtraitement.setText(traitement.getNom());

        //"nouveau-elle ...."
        String accord="Nouvelle";
        if(traitement.getType().getAccord()=='m') {
            accord="Nouveau";
        }
        String prochain = accord+" "+traitement.getType().getDenom();
        TextView tvprochain = (TextView) findViewById(R.id.titrenvelledate);
        tvprochain.setText(prochain);


        //zone selection
        zones = (Spinner) findViewById(R.id.zonenewdate);


        //récupérer zones associées s'il y en a (et garder derniere zone en mémoire pour préselection)
        Zone dernierezone = derniereprise.getZone();
        boolean isdernierezone = dernierezone!=null;
        int indicedernierezone=-1;

        ZoneDAO zdao = new ZoneDAO(this);
        zdao.open();
        listezones = zdao.getZonesFrom(IDTRAITEMENT);
        zdao.close();

        if(listezones.size()>0) {
            List<String> zonesstr = new ArrayList<>();
            int i=0;
            for(Zone z : listezones) {
                if(isdernierezone && z.getId()==dernierezone.getId()) {
                    indicedernierezone=i;
                }
                i++;
                zonesstr.add(z.getIntitule()+" "+z.getCote());
            }

            //lier au Spinner

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, zonesstr);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            zones.setAdapter(adapter);

            //init défaut --> (dernière zone + 1) modulo nombre de zones
            int indiceprochainezone=(indicedernierezone+1)%zones.getCount();
            zones.setSelection(indiceprochainezone);
        } else {
            zonesassoc=false;
            zones.setVisibility(View.GONE);
            TextView nvzonetitre = (TextView) findViewById(R.id.nvzonetitre);
            nvzonetitre.setText("");
        }


        //dosage
        TextView dosagetxt = (TextView) findViewById(R.id.dosagenvdate);
        dosagetxt.setText(traitement.getDosage());

        //fréquence
        EditText freqtxt = (EditText) findViewById(R.id.freqnvdate);
        freqtxt.setText(""+traitement.getFrequence());


        //dernie-e xxx
        accord="Dernière";
        if(traitement.getType().getAccord()=='m') {
            accord="Dernier";
        }

        String dernierstr = accord+" "+traitement.getType().getDenom()+" : ";
        TextView tvdernier = (TextView) findViewById(R.id.titreancdate);
        tvdernier.setText(dernierstr);

        //derniere date
        Date dernieredate=derniereprise.getDateprise();
        cal.setTime(dernieredate);

        int dd = cal.get(Calendar.DAY_OF_WEEK);
        String nomjour = getResources().getStringArray(R.array.jours)[dd-1];
        int numjour = cal.get ( Calendar.DAY_OF_MONTH );

        int nummois=cal.get(Calendar.MONTH);
        String mois  = getResources().getStringArray(R.array.mois)[nummois];
        int annee = cal.get(Calendar.YEAR);

        String datestr=nomjour+" "+numjour+" "+mois+" "+annee;

        TextView tvancdate = (TextView) findViewById(R.id.ancdate);
        tvancdate.setText(datestr);

        //dernière zone
        TextView edzoneprec = (TextView) findViewById(R.id.anczone);
        if(zonesassoc) {
            String zoneprec="";
            if(isdernierezone) {
                zoneprec = "zone : "+dernierezone.getIntitule()+" "+dernierezone.getCote();
            } else {
                zoneprec = "zone : aucune";
            }
            edzoneprec.setText(zoneprec);
        } else {
            edzoneprec.setText("");
        }


    }

    public void enregistrernouvelledate(View view) {

        //recuperer date
        DatePicker dp = (DatePicker) findViewById(R.id.newdate);

        int jour = dp.getDayOfMonth();
        int mois = dp.getMonth();
        int annee = dp.getYear();

        cal.set(annee, mois, jour);
        Date date = cal.getTime();
        Log.d("####################", date.toString());

        //recuperer zone s'il y en a une
        Zone zone = null;

        if(zonesassoc) {
            Spinner zoneselect = (Spinner) findViewById(R.id.zonenewdate);
            String zonestr = zoneselect.getSelectedItem().toString();
            String[] spl = zonestr.split(" ");
            for(Zone z : listezones) {
                if(spl[0].equals(z.getIntitule()) && spl[1].equals(z.getCote())) {
                    zone=z;
                }
            }
        }

        Prise nvprise = new Prise(-1, date, traitement, zone);
        nvprise.enregistrerBD(this);


        //récupérer fréquence et rectifier cas échéant

        boolean ok = true;
        int nvfreq;
        EditText etnvfreq = (EditText) findViewById(R.id.freqnvdate);
        try {
            nvfreq = Integer.valueOf(etnvfreq.getText().toString());
        } catch (Exception e) {
            nvfreq=-1;
            ok=false;
        }

        if(nvfreq<=0) {
            Log.d("Frequence", "----------------------------------");
            Toast.makeText(this, "Entrez une fréquence (nombre positif)",  Toast.LENGTH_SHORT).show();
            ok=false;
        }

        if(ok) {
            if(nvfreq!=traitement.getFrequence()) {
                traitement.setFrequence(nvfreq);
                TraitementDAO tdao = new TraitementDAO(this);
                tdao.open();
                tdao.updateTraitement(traitement);
                tdao.close();
            }
        }

        //affichage DEBUG
        PriseDAO pdao = new PriseDAO(this);
        pdao.open();

        List<String > prises = pdao.getPrisesStr();
        for(String s : prises ) {
            Log.d("+++++++++++++++ PRISE  ", s);
        }

        pdao.close();
        ///////////////////////////////////////////////////

        //calculer prochaine prise
        cal.setTime(date);
        cal.add(Calendar.DATE, traitement.getFrequence());
        Date prochaineprise = cal.getTime();

        //mettre à jour date_renouvellement dans table traitement
        traitement.setDate_renouvellement(prochaineprise);

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        tdao.majDateRenouvellement(traitement);
        tdao.close();

        // supprimer anciens rappels associés au traitement si existent
        RappelDAO radao = new RappelDAO(this);
        radao.open();
        radao.supprimerRappelsDuTraitement(IDTRAITEMENT);
        radao.close();


        //date prochain(s) rappel(s) :
        //recup les preferences
        RappelPrefDAO ddao = new RappelPrefDAO(this);
        ddao.open();
        List<RappelPref> prefrappels = ddao.getRappelPrefFrom(traitement);
        ddao.close();

        //créer les rappels associés
        if(prefrappels.size()>0) {
            for(RappelPref pref : prefrappels) {
                //calculer jour de la prochaine date de rappel
                cal.setTime(prochaineprise);
                cal.add(Calendar.DATE, -pref.getDelai());
                Date prochainrappel = cal.getTime();
                //créer rappel
                Rappel rappel = new Rappel(-1, "traitement", prochainrappel, pref.getHeure(), pref.getDelai(), traitement.getId(), 0, 0);
                rappel.enregistrerBD(this);

                //créer notif
                NotifManager notif = new NotifManager(this);
                notif.creationNotif(rappel);

            }
        }



        //DEBUG affichage //////////////////////////////////////////////
        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> listerappels = rdao.getRappels();
        for(Rappel r : listerappels) {
            Log.d("++++++++++++++++ ", " RAPPELS : "+r.toString());
        }
            //////////////////////////////////////////////////////////////

            //supprimer rappels dont la date est dépassée ??
            long suppr = rdao.supprimerRappelsDepasses();


            rdao.close();

        //Redirection prochaines dates
        Intent intent = new Intent(NouvelleDateActivity.this, ProchaineDateActivity.class);
        finish();
        startActivity(intent);


    }
}
