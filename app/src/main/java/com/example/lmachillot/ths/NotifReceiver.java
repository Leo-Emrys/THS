package com.example.lmachillot.ths;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class NotifReceiver extends BroadcastReceiver {

    public static long ID_NOTIF; // = idrappel
    public static String titrenotif;
    public static String textenotif;
    public static String objet;

    public NotifReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //recup variables
        ID_NOTIF = intent.getLongExtra("idrappel", NotifManager.idrappel);
        titrenotif = intent.getStringExtra("titrenotif");
        if(titrenotif==null) {
            titrenotif = "Rappel THS";
        }
        textenotif = intent.getStringExtra("textenotif");
        if(textenotif==null) {
            textenotif = "Cliquez pour ouvrir";
        }
        objet = intent.getStringExtra("objet");
        if(objet==null) {
            objet = "objet";
        }

        showNotification(context, intent);

        //supprimer rappel
        RappelDAO rdao = new RappelDAO(context);
        rdao.open();
        long rsuppr = rdao.supprimerRappelParId(ID_NOTIF);
        rdao.close();

        Log.d("################", " RAPPEL SUPPRIME nb lignes = "+rsuppr+" ID = "+ID_NOTIF );


    }

    public void showNotification(Context context, Intent intent) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, ProchaineDateActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if(objet.equals("ordonnance")) {
             pi = PendingIntent.getActivity(context, 0, new Intent(context, ConfigOrdoActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else if(objet.equals("stock")) {
            pi = PendingIntent.getActivity(context, 0, new Intent(context, ConfigStockActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(titrenotif)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(titrenotif)
                .setContentText(textenotif)
                .setContentIntent(pi)
                .setAutoCancel(true)

                .build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify((int)ID_NOTIF, notification);
    }
}
