package com.example.lmachillot.ths;

import android.content.Intent;
import android.database.MatrixCursor;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConfigStockActivity extends MenuActivity {

    public static long IDSTOCK=-1;
    public static String REDIRECTION="configstock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_stock);

        //recupérer stocks
        StockDAO sdao = new StockDAO(this);
        sdao.open();
        List<Stock> stocks = sdao.getStocks();
        sdao.close();

        String[] columns = new String[] { "_id", "idstock", "nom", "datedebut", "datefin", "rappel"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);

        int compteur = 1;

        for(Stock s : stocks) {
            //titre
            String titre = getResources().getString(R.string.stockpour)+" ";

            //recup traitements assoc
            titre+=s.getTraitementsString(s, this);


            //date debut affichage
            Date date = s.getDatestock();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String datedebut = getResources().getString(R.string.renvstock)+" "+convertDate(cal);

            //date fin
            cal.add(Calendar.DATE, s.getDuree());
            String datefin = getResources().getString(R.string.finstock)+" "+convertDate(cal);

            //prochain rappel
            RappelDAO rdao = new RappelDAO(this);
            rdao.open();
            Rappel prochrappel = rdao.getProchainRappelStock(s);
            rdao.close();

            String rappelstr="aucun rappel prévu";
            if(prochrappel!=null) {
                cal.setTime(prochrappel.getDaterappel());
                rappelstr = getResources().getString(R.string.prochainrappelordo)+" "+convertDate(cal);
            }


            matrixCursor.addRow(new Object[] { compteur, s.getId()+"", titre, datedebut, datefin, rappelstr });
            compteur++;
        }

        String[] from = new String[] {"idstock", "nom", "datedebut", "datefin", "rappel"};

        int[] to = new int[] { R.id.idstock, R.id.nomstock, R.id.datestock, R.id.finstock, R.id.prochainrappelstock};


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.lignestock, matrixCursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.liststocks);
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

    public void ajouterStock(View view) {
        //verifier qu'il existe au moins un traitement
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        boolean existe = tdao.traitementPresent();
        tdao.close();

        if(existe) {
            Intent intent = new Intent(ConfigStockActivity.this, NewStockActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Veuillez d'abord enregistrer un traitement", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConfigStockActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void supprimerStock(View view) {

        RelativeLayout parent = (RelativeLayout) view.getParent();
        LinearLayout grandparent = (LinearLayout) parent.getParent();
        TextView child = (TextView) grandparent.getChildAt(0);
        long idstock = Long.parseLong(child.getText().toString());

        StockDAO sdao = new StockDAO(this);
        sdao.open();
        sdao.supprimerStock(idstock);
        sdao.close();

        Intent intent = new Intent(ConfigStockActivity.this, ConfigStockActivity.class);
        finish();
        startActivity(intent);
    }

    public void configStock(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        IDSTOCK = Long.parseLong(child.getText().toString());

        Intent intent = new Intent(ConfigStockActivity.this, ConfigRappelsActivity.class);
        intent.putExtra("IDSTOCK", IDSTOCK);
        intent.putExtra("REDIRECTION", REDIRECTION);
        startActivityForResult(intent, 0);
    }

    public void renouvellement(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        IDSTOCK = Long.parseLong(child.getText().toString());

        Intent intent = new Intent(ConfigStockActivity.this, RenouvellementStockActivity.class);
        intent.putExtra("IDSTOCK", IDSTOCK);

        startActivityForResult(intent, 0);

    }
}
