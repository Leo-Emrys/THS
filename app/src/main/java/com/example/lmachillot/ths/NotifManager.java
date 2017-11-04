package com.example.lmachillot.ths;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 26/03/2017.
 */

public class NotifManager {

    //DEBUG
    private int minute=00;
    //////////////

    public Context pContext;
    private AlarmManager am;

    public static String titrenotif="titre";
    public static String textenotif="texte";
    public static long idrappel=-1;
    public static String objet = "";


    public NotifManager(Context pContext) {
        this.pContext=pContext;

    }

    public void creationNotif(Rappel rappel) {
        this.idrappel=rappel.getId();

        Calendar cal = Calendar.getInstance();
        cal.setTime(rappel.getDaterappel());

        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int heure = rappel.getHeure();
        int id = (int)rappel.getId();

        int delai = rappel.getDelai();
        String delaistr=" dans "+delai+" jours";
        if(delai==0) {
            delaistr = "aujourd'hui";
        } else if(delai==1) {
            delaistr = "demain";
        }

        TraitementDAO tdao = new TraitementDAO(pContext);

        Log.e("OBJET////////////", rappel.getObjet());
        Log.e("TEXTE////////////", delaistr);
        Log.e("DELAI////////////", delai+"");

        switch (rappel.getObjet()) {
            case "traitement" :
                titrenotif="Traitement à prendre ";
                Log.e("Titre notif////////////", titrenotif);
                tdao.open();
                String nom = tdao.getNomTraitement(rappel.getIdtraitement());
                tdao.close();
                textenotif="Traitement : "+nom+" à prendre "+delaistr;
                Log.e("Texte notif////////////", textenotif);
                objet="traitement";
                break;
            case "ordonnance" :
                titrenotif="Fin d'ordonnance ";
                TC_OrdoTraitementDAO otdao = new TC_OrdoTraitementDAO(pContext);
                otdao.open();
                List<Long> listeidt = otdao.getIdTraitementsDeOrdo(rappel.getIdordonnance());
                otdao.close();
                String nomtraitements = "";
                for(Long idt : listeidt) {
                    tdao.open();
                    nomtraitements+=tdao.getNomTraitement(idt)+" ";
                    tdao.close();
                }
                textenotif="Ordonnance à renouveller pour "+nomtraitements+delaistr;
                objet="ordonnance";
                break;
            case "stock" :
                titrenotif="Fin de stock";
                StockDAO sdao = new StockDAO(pContext);
                sdao.open();
                Stock stock = sdao.getStock(rappel.getIdstock());
                sdao.close();
                String nom2 = stock.getTraitementsString(stock, pContext);
                textenotif="Stock de "+nom2+" épuisé "+delaistr;
                objet="stock";
                break;
        }


        idrappel=id;

        am = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        ajouterAlarme((int)id, year, month, day, heure, minute);

    }

    private void ajouterAlarme(int id, int year, int month, int day, int hour, int minute)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);

        Log.e("titrenotif////////////", titrenotif);
        Log.e("textenotif////////////", textenotif);
        Log.e("objet////////////", objet+"");

        Intent intent = new Intent(pContext, NotifReceiver.class);
        intent.putExtra("titrenotif", titrenotif);
        intent.putExtra("textenotif", textenotif);
        intent.putExtra("idrappel", idrappel);
        intent.putExtra("objet", objet);

        Log.e("id++++++++++++++++++", id+"");
        PendingIntent operation = PendingIntent.getBroadcast(pContext, id, intent, PendingIntent.FLAG_ONE_SHOT);
        // id --> identifiant de cette alarme particulière
        // utile pour cancel !
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), operation);


    }

    //supprimer alarme
    public void supprimeralarme(long id) {
        idrappel=id;

        int idalarme = (int) id;

        am = (AlarmManager)pContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(pContext, NotifReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(pContext, idalarme, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(sender);

        Log.d("~~~~~~~~~~~~~~~~~~~~~~", "ID ALARME SUPPRIMEE "+idalarme);

    }

}
