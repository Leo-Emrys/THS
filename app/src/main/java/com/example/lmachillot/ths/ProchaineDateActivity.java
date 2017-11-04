package com.example.lmachillot.ths;

import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ProchaineDateActivity extends MenuActivity {

    public static long IDTRAITEMENT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prochaine_date);

        //récupérer traitements
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();

        List<Traitement> liste = tdao.getTraitements();

        tdao.close();

        //DEBUG
        for(Traitement t : liste) {
            Log.d("++++++++++++++++++++++", "Traitement : "+t.toString());
        }

        ////////////////////


        // afficher traitements dynamiquement


        //Affichage DEBUG

        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> rappels = rdao.getRappels();
        rdao.close();

        for(Rappel r : rappels) {
            Log.d("++++++++++++++++++  ", "RAPPEL "+r.toString());

        }


        ////////////////////////////

        if(liste.size()>0) {

            String[] columns = new String[]{"_id", "col1", "col2", "col3", "col4", "col5"};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            startManagingCursor(matrixCursor);

            int compteur = 1;
            for (Traitement t : liste) {


                Log.d("liste : ", t.toString());


                //Recuperer intitule

                String accord = "";
                if (t.getType().getAccord() == 'f') {
                    accord = "e";
                }

                String nomtraitement = t.getNom() + "  ";

                String intitule = "Prochain" + accord + " " + t.getType().getDenom() + " le ";

                //récupérer date

                String datestr = "";
                GregorianCalendar cal = new GregorianCalendar();
                Date date = t.getDate_renouvellement();

                if (date == null) {
                    Log.e("whaaaaaaaaaaaaaaaaaaat", "pas de date renouvellement pour le traitement !!!!!!!!!!");
                    datestr = "Non renseigné";
                } else {
                    cal.setTime(date);
                    int dd = cal.get(Calendar.DAY_OF_WEEK);
                    String nomjour = getResources().getStringArray(R.array.jours)[dd - 1];
                    int numjour = cal.get(Calendar.DAY_OF_MONTH);

                    int nummois = cal.get(Calendar.MONTH);
                    String mois = getResources().getStringArray(R.array.mois)[nummois];
                    int annee = cal.get(Calendar.YEAR);

                    datestr = nomjour + " " + numjour + " " + mois + " " + annee;
                }


                //récupérer date prochain rappel
                String txtprochainrappel = "";


                RappelDAO trdao = new RappelDAO(this);
                trdao.open();
                //(supprimer au passage rappels dépassés)
                trdao.supprimerRappelsDepasses();

                Rappel rappel = trdao.getProchainRappelTraitement(t);

                trdao.close();

                if (rappel != null) {

                    Date daterappel = rappel.getDaterappel();
                    Log.d("~~~~~~~~~~~~~~~~~~~~~", "~~~~~~prochain rappel traitement " + t.getId() + " : " + daterappel);
                    cal.setTime(daterappel);
                    int dw = cal.get(Calendar.DAY_OF_WEEK);
                    String nomj = getResources().getStringArray(R.array.jours)[dw - 1];
                    int numj = cal.get(Calendar.DAY_OF_MONTH);

                    int numm = cal.get(Calendar.MONTH);
                    String month = getResources().getStringArray(R.array.mois)[numm];
                    int year = cal.get(Calendar.YEAR);

                    String rappelstr = nomj + " " + numj + " " + month + " " + year;

                    txtprochainrappel += getResources().getString(R.string.prochainrappeltitre) + " " + rappelstr;


                } else {
                    txtprochainrappel += "Pas de rappel prévu";
                }


                matrixCursor.addRow(new Object[]{compteur, t.getId() + "", nomtraitement, intitule, datestr, txtprochainrappel});
                compteur++;

            }

            String[] from = new String[]{"col1", "col2", "col3", "col4", "col5"};

            int[] to = new int[]{R.id.idtraitementnv, R.id.intnomtraitement, R.id.intprochainedate, R.id.prochdate, R.id.prochainrappel};


            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.ligneprochdate, matrixCursor, from, to, 0);

            ListView lv = (ListView) findViewById(R.id.list);
            lv.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Veuillez d'abord enregistrer un traitement", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProchaineDateActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }

    }


    public void nouvelledate(View view) {

        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        Log.d("++++++++++++++++++", child.getText().toString());
        String idtraitementstr = child.getText().toString();
        IDTRAITEMENT=Long.parseLong(idtraitementstr);

        Intent intent = new Intent(ProchaineDateActivity.this, NouvelleDateActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        startActivityForResult(intent, 0);

    }

    public void configRappel(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        TextView child = (TextView) parent.getChildAt(0);
        Log.d("++++++++++++++++++", child.getText().toString());
        String idtraitementstr = child.getText().toString();
        IDTRAITEMENT=Long.parseLong(idtraitementstr);

        Intent intent = new Intent(ProchaineDateActivity.this, ConfigRappelsActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        startActivityForResult(intent, 0);

    }

    public void modifierTraitement(View view) {
        RelativeLayout parent = (RelativeLayout) view.getParent();
        LinearLayout grandparent = (LinearLayout) parent.getParent();
        TextView child = (TextView) grandparent.getChildAt(0);
        Log.d("++++++++++++++++++", child.getText().toString());

        String idtraitementstr = child.getText().toString();
        IDTRAITEMENT=Long.parseLong(idtraitementstr);


        Intent intent = new Intent(ProchaineDateActivity.this, UpdateTraitementActivity.class);
        intent.putExtra("IDTRAITEMENT", IDTRAITEMENT);
        startActivityForResult(intent, 0);
    }

    //rafraichissement quand on retourne sur l'activité
    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}
