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

public class TC_TraitementZoneDAO extends DAOBase {

    public static String nomtable = "tc_traitement_zone";

    public static String IDZONE = "_id_zone";
    public static String IDTRAITEMENT = "_id_traitement";
    public static String FREQUENCE = "frequence";

    public TC_TraitementZoneDAO(Context pContext) {
        super(pContext);
    }

    public long ajouterTraitementZone(long idzone, long idtraitement, int frequence) {
        ContentValues values = new ContentValues();
        values.put(IDZONE, idzone);
        values.put(IDTRAITEMENT, idtraitement);
        values.put(FREQUENCE, frequence);

        long id=mDb.insert(nomtable, null, values);
        return id;
    }

    public List<TcTraitementZone> getTraitementZones() {
        List<TcTraitementZone> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //récupérer données
                    long idtraitement = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    long idzone = cursor.getLong(cursor.getColumnIndex(IDZONE));
                    int frequence = cursor.getInt(cursor.getColumnIndex(FREQUENCE));

                    //créer un objet Zone et l'ajouter à la liste
                    TcTraitementZone tz = new TcTraitementZone(idtraitement, idzone, frequence);
                    liste.add(tz);

                }
            }
            cursor.close();
        }

        return liste;
    }


    //supprimer une association zone/traitement
    public long deleteTcZoneFromTraitement(long idzone, long idtraitement) {
        return mDb.delete(nomtable, String.format("%s = ? and %s = ?", IDTRAITEMENT, IDZONE), new String[] {idtraitement+"", idzone+""});
    }

    //supprimer toutes les associations de zones à un traitement donné
    public long deleteTcZonesFromTraitement(long idt) {
        return mDb.delete(nomtable, String.format("%s = ?", IDTRAITEMENT), new String[] {idt+""});
    }


    //supprimer toutes les données de la table
    public void deleteAllTcTraitementZone() {
            mDb.delete(nomtable, null, null);
    }

}
