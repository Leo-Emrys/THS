package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmachillot on 15/03/17.
 */

public class ZoneDAO extends DAOBase {

    public String nomtable = "zone";

    public static String ID = "_id";
    public static String INTITULE = "intitule";
    public static String COTE = "cote";

    public ZoneDAO(Context pContext) {
        super(pContext);
    }

    //ajouter une zone
    public long ajouterZone(Zone z) {
        ContentValues values = new ContentValues();
        values.put(INTITULE, z.getIntitule());
        values.put(COTE, z.getCote());
        long id = mDb.insert(nomtable, null, values);

        z.setId(id);
        return id;
    }


    //récupérer toutes les zones existantes
    public List<Zone> getZones() {
        List<Zone> zones = new ArrayList<Zone>();

        String requete = "SELECT * FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //récupérer données
                    long id = cursor.getLong(0);
                    String intitule = cursor.getString(cursor.getColumnIndex(INTITULE));
                    String cote = cursor.getString(cursor.getColumnIndex(COTE));

                    //créer un objet Zone et l'ajouter à la liste
                    Zone zone = new Zone(id, intitule, cote);
                    zones.add(zone);

                }
            }
            cursor.close();
        }

        return zones;
    }


    //récupérer tous les intitulés distincts des zones
    public List<String> getIntitulesZones() {
        List<String> liste = new ArrayList<>();

        String requete = "SELECT DISTINCT "+ INTITULE +" FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    liste.add(cursor.getString(cursor.getColumnIndex(INTITULE)));
                }
            }

            cursor.close();
        }


        return liste;
    }


    //récupérer zones associées à un traitement
    public List<Zone> getZonesFrom(long idtraitement) {
        List<Zone> zones = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" INNER JOIN "+TC_TraitementZoneDAO.nomtable+
                    " ON "+TC_TraitementZoneDAO.IDZONE+"="+ID+
                    " WHERE "+TC_TraitementZoneDAO.IDTRAITEMENT+"="+idtraitement;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    String intitule = cursor.getString(cursor.getColumnIndex(INTITULE));
                    String cote = cursor.getString(cursor.getColumnIndex(COTE));
                    long idzone = cursor.getLong(0);

                    Zone zone = new Zone(idzone, intitule, cote);
                    zones.add(zone);

                }
            } else {
                Log.d("PROBLEME CURSOR getZonesFrom", "---------- cursor=0");
            }
        } else {
            Log.d("PROBLEME CURSOR getZonesFrom", "------------ cursor null");
            cursor.close();
        }

        return zones;
    }

    //DEBUG
    public void supprimerZonesNonPredef() {
        String req = "DELETE FROM zone WHERE _id > 11";
        mDb.execSQL(req);
    }
}
