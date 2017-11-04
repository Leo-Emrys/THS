package com.example.lmachillot.ths;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SetRappelsActivity extends MenuActivity {

    public static long IDTRAITEMENT = -1;
    public static long IDORDONNANCE = -1;
    public static long IDSTOCK = -1;
    public static String REDIRECTION="prochainedate";
    public static long CREATION = -1;
    private Traitement traitement;
    private Ordonnance ordonnance;
    private Stock stock;

    @Override
    public void onBackPressed() {
        if(CREATION>0) {
            Intent intent = new Intent(SetRappelsActivity.this, PremierePriseActivity.class);
            finish();
            startActivity(intent);
        } else
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_rappels);

        //récupérer variables
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);
        IDORDONNANCE = intent.getLongExtra("IDORDONNANCE", -1);
        IDSTOCK = intent.getLongExtra("IDSTOCK", -1);
        String var_redir = intent.getStringExtra("REDIRECTION");
        if(var_redir!=null) {
            REDIRECTION=var_redir;
        }
        long crea = intent.getLongExtra("CREATION", -1);
        if(crea!=0)
            CREATION=crea;


        if(IDTRAITEMENT<0) {
            if(IDORDONNANCE>0) {
                //recuperer ordonnance
                OrdonnanceDAO odao = new OrdonnanceDAO(this);
                odao.open();
                ordonnance = odao.getOrdonnance(IDORDONNANCE);
                odao.close();

                //afficher nom ordonnance
                TextView textView = (TextView) findViewById(R.id.traitementrappel);
                textView.setText(getResources().getString(R.string.pourlordonnance) + " " + ordonnance.getNom());

            }else if(IDSTOCK>0) {

                //recuperer stock
                StockDAO sdao = new StockDAO(this);
                sdao.open();
                stock = sdao.getStock(IDSTOCK);
                sdao.close();

                //récupérer traitement(s) associé(s)


                String nomstraitements = stock.getTraitementsString(stock, this);


                //afficher nom stock
                TextView textView = (TextView) findViewById(R.id.traitementrappel);
                textView.setText(getResources().getString(R.string.pourstock) + " " + nomstraitements);

            } else {
                Toast.makeText(this, "pas de traitement, ordonnance ou stock associée (s)!", Toast.LENGTH_SHORT).show();

            }
        } else {
            //récupérer traitement
            TraitementDAO tdao = new TraitementDAO(this);
            tdao.open();
            traitement = tdao.getTraitementParId(IDTRAITEMENT);
            tdao.close();

            if(traitement==null) {
                Log.d("-----------------", "impossible de récupérer le traitement "+IDTRAITEMENT);
                Toast.makeText(this, "Erreur Traitement", Toast.LENGTH_SHORT).show();
            }

            //afficher nom traitement
            TextView textView = (TextView) findViewById(R.id.traitementrappel);
            textView.setText(getResources().getString(R.string.pourtraitement)+" "+traitement.getNom());

        }



    }


    public void passer(View view) {

        //affichage DEBUG
        Log.d("ID ordonnance /////////", IDORDONNANCE+"");



        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> listerappels = rdao.getRappels();
        rdao.close();

        for(Rappel r : listerappels) {
            Log.d("*************** ", "RAPPELS ENREGISTRES "+r.toString());
        }

        ////////////////////////
        Intent intent=null;
        Log.d("REDIRECTION", REDIRECTION);
        if(REDIRECTION.equals("prochainedate")) {
            intent = new Intent(SetRappelsActivity.this, ProchaineDateActivity.class);
        } else if(REDIRECTION.equals("configrappels")) {
            intent = new Intent(SetRappelsActivity.this, ConfigRappelsActivity.class);
        } else if(REDIRECTION.equals("configordo")) {
            intent = new Intent(SetRappelsActivity.this, ConfigOrdoActivity.class);
        } else if(REDIRECTION.equals("configstock")) {
            intent = new Intent(SetRappelsActivity.this, ConfigStockActivity.class);
        }
        finish();
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);
        intent.putExtra("IDSTOCK", IDSTOCK);
        startActivityForResult(intent, 0);
    }


    public Rappel enregistrerRappel(int nbjours, int heure) {
        Date prochainedate=null;
        String objet="";
        GregorianCalendar cal = new GregorianCalendar();


        if(IDTRAITEMENT>0) {
            //récupérer prochaine date traitement
            prochainedate= traitement.getDate_renouvellement();
            objet="traitement";

        } else if(IDORDONNANCE>0) {
            //calcul date fin ordonnance
            cal.setTime(ordonnance.getDateordo());
            cal.add(Calendar.DATE, ordonnance.getDuree());

            prochainedate=cal.getTime();
            objet="ordonnance";
        } else if(IDSTOCK>0) {
            //calcul date fin stock
            cal.setTime(stock.getDatestock());
            cal.add(Calendar.DATE, stock.getDuree());

            prochainedate = cal.getTime();
            objet="stock";
        }


        //soustraire nombre de jours
        cal.setTime(prochainedate);
        cal.add(Calendar.DATE, -nbjours);
        Date daterappel = cal.getTime();

        Log.d("+++++++++++++++++++++++", "Date du prochain rappel : "+daterappel);

        //créer Rappel
        Rappel rappel = null;
        if(IDTRAITEMENT>0) {
            rappel = new Rappel(-1, objet, daterappel, heure, nbjours, IDTRAITEMENT, 0, 0);
        } else if(IDORDONNANCE>0) {
            rappel = new Rappel(-1, objet, daterappel, heure, nbjours, 0, IDORDONNANCE, 0);
            Log.d("RAPPEL CREE ORDO ????", rappel.toString());
        } else if(IDSTOCK>0) {
            rappel = new Rappel(-1, objet, daterappel, heure, nbjours, 0, 0, IDSTOCK);
        }



        //enregistrement BD
        rappel.enregistrerBD(this);

        //creation notif
        NotifManager notif = new NotifManager(this);
        notif.creationNotif(rappel);


        return rappel;
    }


    public boolean traitementRappel() {

        boolean ok = true;

        //récupérer champ jours
        EditText ednbjours = (EditText) findViewById(R.id.nbjours);
        String nbjoursstr = ednbjours.getText().toString();
        int nbjours = -1;

        try {
            nbjours = Integer.parseInt(nbjoursstr);
        } catch(Exception e) {
            Toast.makeText(this, "Entrer un nombre de jours", Toast.LENGTH_SHORT).show();
            ok=false;
        }

        if(nbjours<0) {
            ok=false;
        }

        int heure = -1;

        EditText edheure = (EditText) findViewById(R.id.heurerappel);
        String heurestr = edheure.getText().toString();

        try {
            heure = Integer.parseInt(heurestr);
        } catch(Exception e) {
            Toast.makeText(this, "Entrer une heure (nombre entier)", Toast.LENGTH_SHORT).show();
            ok=false;
        }

        if(heure<0) {
            ok=false;
        }

        if(ok) {
            // verifier si il n'existe pas déjà un rappel à cette date / heure pour ce traitement avant de créer rappel
            boolean existedeja=true;

            RappelDAO rdao = new RappelDAO(this);
            rdao.open();
            if(IDTRAITEMENT>0) {
                existedeja = rdao.rappelExiste(traitement, nbjours, heure);
            } else if (IDORDONNANCE>0) {
                existedeja = rdao.rappelExiste(ordonnance, nbjours, heure);
            } else if (IDSTOCK>0) {
                existedeja = rdao.rappelExiste(stock, nbjours, heure);
            }
            rdao.close();

            if(!existedeja) {
                enregistrerRappel(nbjours, heure);

                RappelPref rp=null;
                if(IDTRAITEMENT>0) {
                    //enregistrer delai dans rappelpref (préférences rappel pour traitement)
                    rp = new RappelPref(-1, nbjours, heure, traitement, null, null);

                } else if(IDORDONNANCE>0) {
                    rp = new RappelPref(-1, nbjours, heure, null, ordonnance, null);
                } else if(IDSTOCK>0) {
                    rp = new RappelPref(-1, nbjours, heure, null, null, stock);
                }

                RappelPrefDAO dpdao = new RappelPrefDAO(this);
                dpdao.open();
                dpdao.ajouterRappelPref(rp);
                dpdao.close();

                Toast.makeText(this, "Rappel créé", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ce rappel existe déjà !", Toast.LENGTH_SHORT).show();
            }
        }



        //affichage DEBUG
        RappelPrefDAO dpdao = new RappelPrefDAO(this);
        dpdao.open();
        List<String> liste = dpdao.getRappelPrefStr();
        dpdao.close();

        for (String d : liste) {
            Log.d("+++++++++++++++++++++", "+ DELAI PREF TABLE "+ d);
        }

        //////////////////////

        return ok;

    }

    public void recharger() {

        //affichages DEBUG /////////////////////////////////////////////
        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> listerappels = rdao.getRappels();
        rdao.close();

        for(Rappel r : listerappels) {
            Log.d("*************** ", "RAPPELS ENREGISTRES "+r.toString());
        }
        ///////////////////////////////////////////////////

        Intent intent = new Intent(SetRappelsActivity.this, SetRappelsActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);
        intent.putExtra("IDSTOCK", IDSTOCK);
        intent.putExtra("REDIRECTION", REDIRECTION);
        finish();
        startActivity(intent);

    }

    public void validerEtContinuer(View view) {

        traitementRappel();


        recharger();
    }


    public void validerTerminer(View view) {

        if(traitementRappel()) {
            passer(view);
        } else {
            recharger();
        }

    }

}
