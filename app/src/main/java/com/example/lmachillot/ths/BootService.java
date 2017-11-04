package com.example.lmachillot.ths;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by Leonard on 23/07/2017.
 */
public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("+++++++++++++++", "Service démarré !!!!!!!!!!!!");
        // récupérer notifications prévues
        RappelDAO rdao = new RappelDAO(this);
        rdao.open();
        List<Rappel> liste = rdao.getRappels();

        ///DEBUG
        if(liste.size()>0) {
            for (Rappel r : liste) {
                Log.e("RAPPEL !!!!!!", r.toString());
            }
        } else {
            Log.e("Liste vide", "000000000000000000000000000");
        }
        //DEBUG

        rdao.close();

        // recréer les alarmes pour les notifications
        if(liste.size()>0) {
            NotifManager notifManager = new NotifManager(this);
            for (Rappel r : liste) {
                notifManager.creationNotif(r);
            }
        } else {
            Log.e("Liste vide", "000000000000000000000000000");
        }
    }
}
