package com.example.lmachillot.ths;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RenouvellementOrdoActivity extends AppCompatActivity {

    public static long IDORDONNANCE=-1;
    public static String REDIRECTION="configordo";

    private Ordonnance ordonnance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renouvellement_ordo);

        IDORDONNANCE = getIntent().getLongExtra("IDORDONNANCE", IDORDONNANCE);

        //recup ordonnance
        OrdonnanceDAO odao = new OrdonnanceDAO(this);
        odao.open();
        ordonnance = odao.getOrdonnance(IDORDONNANCE);
        odao.close();

        //afficher nom
        TextView tvnom = (TextView) findViewById(R.id.nomordorenv);
        tvnom.setText(ordonnance.getNom());

        //préremplir durée
        EditText edduree = (EditText) findViewById(R.id.dureeordorenv);
        edduree.setText(ordonnance.getDuree()+"");

    }

    public void enregistrerRenouvellement(View view) {
        EditText edduree = (EditText) findViewById(R.id.dureeordorenv);
        int duree = Integer.parseInt(edduree.getText().toString());

        DatePicker dp = (DatePicker) findViewById(R.id.nvdateordo);
        int year = dp.getYear();
        int mois = dp.getMonth();
        int jour = dp.getDayOfMonth();

        Calendar cal = Calendar.getInstance();
        cal.set(year, mois, jour);
        Date date = cal.getTime();

        ordonnance.setDateordo(date);
        ordonnance.setDuree(duree);

        OrdonnanceDAO odao = new OrdonnanceDAO(this);
        odao.open();
        long modif = odao.updateOrdonnance(ordonnance);
        odao.close();

        // supprimer anciens rappels associés à l'ordonnance si existent
        RappelDAO radao = new RappelDAO(this);
        radao.open();
        radao.supprimerRappelsDelOrdo(IDORDONNANCE);
        radao.close();


        //date prochain(s) rappel(s) :
        //recup les preferences
        RappelPrefDAO ddao = new RappelPrefDAO(this);
        ddao.open();
        List<RappelPref> prefrappels = ddao.getRappelPrefFrom(ordonnance);
        ddao.close();

        //créer les rappels associés
        if(prefrappels.size()>0) {
            for(RappelPref pref : prefrappels) {
                //calculer jour de la prochaine date de rappel
                cal.setTime(date);
                cal.add(Calendar.DATE, ordonnance.getDuree());
                cal.add(Calendar.DATE, -pref.getDelai());
                Date prochainrappel = cal.getTime();
                //créer rappel
                Rappel rappel = new Rappel(-1, "ordonnance", prochainrappel, pref.getHeure(), pref.getDelai(), 0, IDORDONNANCE, 0);
                rappel.enregistrerBD(this);

                //créer notif
                NotifManager notif = new NotifManager(this);
                notif.creationNotif(rappel);

            }
        }


        //verif DEBUG
        Log.d("++++++++++++++++++++", "Ligne modifiée : "+modif);

        ////////////////////

        Intent intent = new Intent(RenouvellementOrdoActivity.this, ConfigOrdoActivity.class);
        intent.putExtra("IDORDONNANCE", IDORDONNANCE);
        intent.putExtra("REDIRECTION", REDIRECTION);
        finish();
        startActivity(intent);
    }
}
