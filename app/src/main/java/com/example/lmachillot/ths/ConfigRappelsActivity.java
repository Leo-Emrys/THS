package com.example.lmachillot.ths;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConfigRappelsActivity extends MenuActivity {

    public static long IDTRAITEMENT=-1;
    public static long IDORDONNANCE=-1;
    public static long IDSTOCK = -1;
    public static String REDIRECTION="configrappels";
    private Traitement traitement;
    private Ordonnance ordonnance;
    private Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_rappels);

        //récupérer variable
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);
        IDORDONNANCE = intent.getLongExtra("IDORDONNANCE", -1);
        IDSTOCK = intent.getLongExtra("IDSTOCK", -1);

        String var_redir = intent.getStringExtra("REDIRECTION");
        if(var_redir!=null) {
            REDIRECTION=var_redir;
        }


        if(IDTRAITEMENT>0) {
            // recuperer traitement
            TraitementDAO tdao = new TraitementDAO(this);
            tdao.open();
            traitement = tdao.getTraitementParId(IDTRAITEMENT);
            tdao.close();

            //completer titre avec nom traitement
            TextView tvtitre = (TextView) findViewById(R.id.titreconfigrappels);
            tvtitre.setText(getResources().getString(R.string.titre_configrappeltraitement) + " " + traitement.getNom());
        } else if(IDORDONNANCE>0) {
            //récupérer ordonnance
            OrdonnanceDAO odao = new OrdonnanceDAO(this);
            odao.open();
            ordonnance = odao.getOrdonnance(IDORDONNANCE);
            odao.close();

            //compléter titre
            TextView tvtitre = (TextView) findViewById(R.id.titreconfigrappels);
            tvtitre.setText(getResources().getString(R.string.titre_configrappelordo) + " " + ordonnance.getNom());

        } else if(IDSTOCK>0) {
            //récupérer stock
            StockDAO sdao = new StockDAO(this);
            sdao.open();
            stock = sdao.getStock(IDSTOCK);
            sdao.close();

            //récupérer traitements associés au stock
            String titre = getResources().getString(R.string.titre_configrappelstock)+" ";
            titre+=stock.getTraitementsString(stock, this);

            //compléter titre
            TextView tvtitre = (TextView) findViewById(R.id.titreconfigrappels);
            tvtitre.setText(titre);

        } else {
            Toast.makeText(this, "pas de traitement, stock ni ordonnance associée (c)!", Toast.LENGTH_SHORT).show();
            // redirection ?
            Intent intent2 = new Intent(ConfigRappelsActivity.this, MainActivity.class);
            finish();
            startActivity(intent2);
        }




        //recuperer rappels + suppr rappels depassés
        List<Rappel> rappels = new ArrayList<>();
        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        if(IDTRAITEMENT>0) {
            rappels = rdao.getRappelsFromIdTraitement(IDTRAITEMENT);
        } else if (IDORDONNANCE>0) {
            rappels = rdao.getRappelsFromIdOrdo(IDORDONNANCE);
        } else if (IDSTOCK>0) {
            rappels = rdao.getRappelsFromIdStock(IDSTOCK);
        }
        rdao.supprimerRappelsDepasses();
        rdao.close();

        String[] columns = new String[] { "_id", "date", "nbjours", "idrappel"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);

        int compteur = 1;

        for (Rappel r : rappels) {
            //recuperer date
            Date date = r.getDaterappel();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int jourint = cal.get(Calendar.DAY_OF_WEEK);
            int numjour = cal.get(Calendar.DAY_OF_MONTH);
            int moisint = cal.get(Calendar.MONTH);
            int annee = cal.get(Calendar.YEAR);

            String jour = getResources().getStringArray(R.array.jours)[jourint-1];
            String mois = getResources().getStringArray(R.array.mois) [moisint];

            String datestr = jour+" "+numjour+" "+mois+" "+annee+" à "+r.getHeure()+"h";

            //recuperer delai
            int delai = r.getDelai();
            String delaistr = "("+delai+" "+getResources().getString(R.string.joursavantrappel);

            //recup id
            String id = r.getId()+"";

            matrixCursor.addRow(new Object[] { compteur, datestr, delaistr, id });
            compteur++;

        }


        String[] from = new String[] {"date", "nbjours", "idrappel"};

        int[] to = new int[] { R.id.daterappel, R.id.nbjoursavant, R.id.idrappel};


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.lignerappel, matrixCursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.listrappels);
        lv.setAdapter(adapter);

    }

    public void recharger() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public void supprimerRappel(View view) {

        //recup rappel
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        Log.d("++++++++++++++++++ ", "idrappel  "+child.getText().toString());
        long idrappel = Long.parseLong(child.getText().toString());


       //supprimer rappel dans BD + alarme associée

        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        Rappel rappel = rdao.getRappel(idrappel);
        //recuperer delai / heure
        int delai = rdao.getDelaiFrom(idrappel);
        int heure = rdao.getHeureFrom(idrappel);
        long supprime = rdao.supprimerRappelParId(idrappel);
        rdao.close();

        NotifManager notif = new NotifManager(this);
        notif.supprimeralarme(idrappel);

        if(supprime>0) {
            Log.d("***************** ", "supprimé "+supprime+" lignes");
            Toast.makeText(this, "Rappel supprimé", Toast.LENGTH_SHORT).show();
        }

        RappelPrefDAO ddao = new RappelPrefDAO(this);
        ddao.open();

        if(IDTRAITEMENT>0) {
            //recupérer rappelpref associé
            RappelPref dp = ddao.getRappelPrefFrom(traitement, delai, heure);
            //supprimer delaipref si existe
            if(dp!=null) {
                ddao.supprimerRappelPref(dp);
            } else {
                Log.d(" ---------------------", "  pas de pref pour idtraitement : "+traitement.getId()+" delai : "+delai+" heure : "+heure);
            }
        } else if(IDORDONNANCE>0) {
            RappelPref dp = ddao.getRappelPrefFrom(ordonnance, delai, heure);
            if(dp!=null) {
                ddao.supprimerRappelPref(dp);
            } else {
                Log.d(" ---------------------", "  pas de pref pour idordo : "+ordonnance.getId()+" delai : "+delai+" heure : "+heure);
            }
        } else if(IDSTOCK>0) {
            RappelPref dp = ddao.getRappelPrefFrom(stock, delai, heure);
            if(dp!=null) {
                ddao.supprimerRappelPref(dp);
            } else {
                Log.d(" ---------------------", "  pas de pref pour idstock : "+stock.getId()+" delai : "+delai+" heure : "+heure);
            }

        }
        ddao.close();

        //affichage DEBUG
        rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> liste = rdao.getRappels();
        rdao.close();

        for(Rappel r : liste) {
            Log.d("+++++++++++++++++++++  ", "RAPPEL "+r.toString());
        }

        RappelPrefDAO rpao = new RappelPrefDAO(this);
        rpao.open();
        List<String> delaispref = rpao.getRappelPrefStr();
        rpao.close();

        for(String d : delaispref) {
            Log.d("DDDDDDDDDDDDDDDDDDDD", "  DELAIS PREF "+d);
        }

        ////////////////////////////////


        //recharger
        recharger();
    }


    public void ajouterRappel(View view) {
        Intent intent = new Intent(ConfigRappelsActivity.this, SetRappelsActivity.class);
        finish();
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);
        intent.putExtra("IDSTOCK", IDSTOCK);
        intent.putExtra("REDIRECTION", REDIRECTION);
        startActivityForResult(intent, 0);
    }
}
