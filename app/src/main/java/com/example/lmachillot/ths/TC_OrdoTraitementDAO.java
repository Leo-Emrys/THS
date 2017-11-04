package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonard on 26/03/2017.
 */

public class TC_OrdoTraitementDAO extends DAOBase {

    public static String nomtable = "tc_ordo_traitement";
    public static String IDTRAITEMENT = "_id_traitement";
    public static String IDORDONNANCE = "_id_ordonnance";

    public TC_OrdoTraitementDAO(Context pContext) {
        super(pContext);
    }

    public long ajouterTcTraitementOrdo(long idtraitement, long idordo) {
        ContentValues values = new ContentValues();
        values.put(IDORDONNANCE, idordo);
        values.put(IDTRAITEMENT, idtraitement);

        return mDb.insert(nomtable, null, values);
    }

    public List<Long> getIdTraitementsDeOrdo(long idordo) {
        List<Long> liste = new ArrayList<>();
        String requete = "SELECT "+IDTRAITEMENT+" FROM "+nomtable+" WHERE "+IDORDONNANCE+"="+idordo;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if (cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long idt = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    liste.add(idt);
                }
            } else {
                Log.d("---------------------", "pas de traitement assoc à "+idordo);
            }

            cursor.close();
        }


        return liste;
    }

    public List<String> afficherEntrees() {
        List<String> liste = new ArrayList<>();
        String req = "SELECT * FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long idt = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    long ido = cursor.getLong(cursor.getColumnIndex(IDORDONNANCE));
                    liste.add("// IDTRAITEMENT :: "+idt+" IDORDO :: "+ido);
                }
            }
            cursor.close();
        }
        return liste;
    }
}
