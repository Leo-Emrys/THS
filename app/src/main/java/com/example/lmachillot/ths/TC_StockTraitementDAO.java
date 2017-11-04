package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonard on 23/07/2017.
 */

public class TC_StockTraitementDAO extends DAOBase {

        public static String nomtable = "tc_stock_traitement";
        public static String IDTRAITEMENT = "_id_traitement";
        public static String IDSTOCK = "_id_stock";

    public TC_StockTraitementDAO(Context pContext) {
        super(pContext);
    }

    public long ajouterTcTraitementStock(long idtraitement, long idstock) {
        ContentValues values = new ContentValues();
        values.put(IDSTOCK, idstock);
        values.put(IDTRAITEMENT, idtraitement);

        return mDb.insert(nomtable, null, values);
    }

    public List<Long> getIdTraitementsDeStock(long idstock) {
        List<Long> liste = new ArrayList<>();
        String requete = "SELECT "+IDTRAITEMENT+" FROM "+nomtable+" WHERE "+IDSTOCK+"="+idstock;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if (cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long idt = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    liste.add(idt);
                }
            } else {
                Log.d("---------------------", "pas de traitement assoc à "+idstock);
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
                    long ido = cursor.getLong(cursor.getColumnIndex(IDSTOCK));
                    liste.add("// IDTRAITEMENT :: "+idt+" IDSTOCK :: "+ido);
                }
            }
            cursor.close();
        }
        return liste;
    }

}
