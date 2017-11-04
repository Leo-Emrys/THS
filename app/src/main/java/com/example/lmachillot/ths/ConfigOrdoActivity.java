package com.example.lmachillot.ths;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConfigOrdoActivity extends MenuActivity {

    public static long IDORDONNANCE=-1;
    public static String REDIRECTION="configordo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_ordo);

        //recupérer ordonnances
        OrdonnanceDAO odao = new OrdonnanceDAO(this);
        odao.open();
        List<Ordonnance> ordos = odao.getOrdonnances();
        odao.close();



        String[] columns = new String[] { "_id", "idordo", "nom", "datedebut", "datefin", "traitements", "rappel"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);

        int compteur = 1;

        for(Ordonnance o : ordos) {

            //date debut affichage
            Date date = o.getDateordo();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);


            String datedebut = getResources().getString(R.string.delivranceordo)+" "+convertDate(cal);

            //date fin
            cal.add(Calendar.DATE, o.getDuree());

            String datefin = getResources().getString(R.string.finordo)+" "+convertDate(cal);

            //traitements associés
            TC_OrdoTraitementDAO otdao = new TC_OrdoTraitementDAO(this);
            otdao.open();
            List<Long> idtraitements = otdao.getIdTraitementsDeOrdo(o.getId());
            otdao.close();

            String traitementsassoc=getResources().getString(R.string.traitementsordo);
            if(idtraitements.size()>1) {
                traitementsassoc+="s";
            }
            traitementsassoc+=" : ";

            TraitementDAO tdao = new TraitementDAO(this);
            tdao.open();
            for(Long id : idtraitements) {
                traitementsassoc+=tdao.getNomTraitement(id)+" \n";
            }
            traitementsassoc=traitementsassoc.substring(0, traitementsassoc.length()-2);
            tdao.close();


            //prochain rappel
            RappelDAO rdao = new RappelDAO(this);
            rdao.open();
            Rappel prochrappel = rdao.getProchainRappelOrdo(o);
            rdao.close();

            String rappelstr="aucun rappel prévu";
            if(prochrappel!=null) {
                cal.setTime(prochrappel.getDaterappel());
                rappelstr = getResources().getString(R.string.prochainrappelordo)+" "+convertDate(cal);
            }


            matrixCursor.addRow(new Object[] { compteur, o.getId()+"", o.getNom(), datedebut, datefin, traitementsassoc, rappelstr });


            compteur++;
        }

        String[] from = new String[] {"idordo", "nom", "datedebut", "datefin", "traitements", "rappel"};

        int[] to = new int[] { R.id.idordo, R.id.nomordo, R.id.dateordo, R.id.finordo, R.id.traitementsordo, R.id.prochainrappelordo};


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.ligneordo, matrixCursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.listordos);
        lv.setAdapter(adapter);


    }

    private String convertDate(Calendar cal) {

        int jourint = cal.get(Calendar.DAY_OF_WEEK);
        int numjour = cal.get(Calendar.DAY_OF_MONTH);
        int moisint = cal.get(Calendar.MONTH);
        int annee = cal.get(Calendar.YEAR);

        String jour = getResources().getStringArray(R.array.jours)[jourint-1];
        String mois = getResources().getStringArray(R.array.mois) [moisint];

        String datestr = jour+" "+numjour+" "+mois+" "+annee;


        return datestr;
    }


    public void ajouterOrdo(View view) {
        //verifier qu'il existe au moins un traitement
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        boolean existe = tdao.traitementPresent();
        tdao.close();

        if(existe) {
            Intent intent = new Intent(ConfigOrdoActivity.this, NewOrdonnanceActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Veuillez d'abord enregistrer un traitement", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConfigOrdoActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void configRappel(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        IDORDONNANCE = Long.parseLong(child.getText().toString());
        REDIRECTION="configrappels";

        Intent intent = new Intent(ConfigOrdoActivity.this, ConfigRappelsActivity.class);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);
        intent.putExtra("REDIRECTION", REDIRECTION);
        startActivityForResult(intent, 0);
    }

    public void renouvellement(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        IDORDONNANCE = Long.parseLong(child.getText().toString());

        Intent intent = new Intent(ConfigOrdoActivity.this, RenouvellementOrdoActivity.class);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);

        startActivityForResult(intent, 0);

    }

    public void supprimerOrdonnance(View view) {

        RelativeLayout parent = (RelativeLayout) view.getParent();
        LinearLayout grandparent = (LinearLayout) parent.getParent();
        TextView child = (TextView) grandparent.getChildAt(0);
        long idordo = Long.parseLong(child.getText().toString());

        OrdonnanceDAO odao = new OrdonnanceDAO(this);
        odao.open();
        odao.supprimerOrdonnance(idordo);
        odao.close();

        Intent intent = new Intent(ConfigOrdoActivity.this, ConfigOrdoActivity.class);
        finish();
        startActivity(intent);
    }
}
