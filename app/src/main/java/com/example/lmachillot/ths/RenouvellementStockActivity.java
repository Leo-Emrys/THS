package com.example.lmachillot.ths;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RenouvellementStockActivity extends MenuActivity {

    public static long IDSTOCK=-1;
    public static String REDIRECTION="configstock";

    private Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renouvellement_stock);

        IDSTOCK = getIntent().getLongExtra("IDSTOCK", -1);

        //recup stock
        StockDAO sdao = new StockDAO(this);
        sdao.open();
        stock = sdao.getStock(IDSTOCK);
        sdao.close();

        //afficher traitements assoc
        String traitements = stock.getTraitementsString(stock, this);

        TextView tvnom = (TextView) findViewById(R.id.traitementstock);
        tvnom.setText(getResources().getString(R.string.renvstocksstitre)+" "+traitements);


    }

    public void enregistrerRenouvellement(View view) {
        EditText edduree = (EditText) findViewById(R.id.dureestockrenv);
        int duree = Integer.parseInt(edduree.getText().toString());

        DatePicker dp = (DatePicker) findViewById(R.id.nvdatestock);
        int year = dp.getYear();
        int mois = dp.getMonth();
        int jour = dp.getDayOfMonth();

        Calendar cal = Calendar.getInstance();
        cal.set(year, mois, jour);
        Date date = cal.getTime();

        stock.setDatestock(date);
        stock.setDuree(duree);

        StockDAO sdao = new StockDAO(this);
        sdao.open();
        long modif = sdao.updateStock(stock);
        sdao.close();

        // supprimer anciens rappels associés au stock si existent
        RappelDAO radao = new RappelDAO(this);
        radao.open();
        radao.supprimerRappelsDuStock(IDSTOCK);
        radao.close();


        //date prochain(s) rappel(s) :
        //recup les preferences
        RappelPrefDAO ddao = new RappelPrefDAO(this);
        ddao.open();
        List<RappelPref> prefrappels = ddao.getRappelPrefFrom(stock);
        ddao.close();

        //créer les rappels associés
        if(prefrappels.size()>0) {
            for(RappelPref pref : prefrappels) {
                //calculer jour de la prochaine date de rappel
                cal.setTime(date);
                cal.add(Calendar.DATE, stock.getDuree());
                cal.add(Calendar.DATE, -pref.getDelai());
                Date prochainrappel = cal.getTime();
                //créer rappel
                Rappel rappel = new Rappel(-1, "stock", prochainrappel, pref.getHeure(), pref.getDelai(), 0, 0, IDSTOCK);
                rappel.enregistrerBD(this);

                //créer notif
                NotifManager notif = new NotifManager(this);
                notif.creationNotif(rappel);

            }
        }


        //verif DEBUG
        Log.d("++++++++++++++++++++", "Ligne modifiée : "+modif);

        ////////////////////

        Intent intent = new Intent(RenouvellementStockActivity.this, ConfigStockActivity.class);
        intent.putExtra("IDSTOCK", IDSTOCK);
        intent.putExtra("REDIRECTION", REDIRECTION);
        finish();
        startActivity(intent);
    }
}
