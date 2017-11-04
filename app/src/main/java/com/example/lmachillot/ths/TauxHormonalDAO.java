package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 26/03/2017.
 */

public class TauxHormonalDAO extends DAOBase {

    public static String nomtable = "tauxhormonal";
    public static String ID = "_id";
    public static String DATE = "dateanalyse";
    public static String HORMONE = "hormone";
    public static String TAUX = "taux";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public TauxHormonalDAO(Context pContext) {
        super(pContext);
    }


    public long ajouterTaux(TauxHormonal t) {
        ContentValues values = new ContentValues();
        values.put(TAUX, t.getTaux());
        values.put(HORMONE, t.getHormone());

        java.sql.Date datesql = new java.sql.Date(t.getDate().getTime());

        values.put(DATE, datesql.toString());

        long id = mDb.insert(nomtable, null, values);

        t.setId(id);

        return id;
    }


    private List<TauxHormonal> constrTauxFrom(Cursor cursor) {
        List<TauxHormonal> liste = new ArrayList<>();

        while (cursor.moveToNext()) {

            long id = cursor.getLong(0);
            String datestr = cursor.getString(cursor.getColumnIndex(DATE));
            double taux = cursor.getDouble(cursor.getColumnIndex(TAUX));
            String hormone = cursor.getString(cursor.getColumnIndex(HORMONE));


            Date date = null;

            try {
                date = format.parse(datestr);
            } catch (ParseException e) {
                Log.d("////////////////", "erreur format date stock DAO");
                e.printStackTrace();
            }

            liste.add(new TauxHormonal(id, hormone, taux, date));
        }

        return liste;
    }

    public List<TauxHormonal> getTauxHormonaux() {
        List<TauxHormonal> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" ORDER BY "+DATE;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                liste = constrTauxFrom(cursor);

            }

            cursor.close();
        }

        return liste;
    }

    public TauxHormonal getTauxHormonal(long idtaux) {
        TauxHormonal taux = null;

        String req = "SELECT * FROM "+nomtable+" WHERE "+ID+"="+idtaux;
        Cursor cursor = mDb.rawQuery(req, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                taux = constrTauxFrom(cursor).get(0);
            }

            cursor.close();
        }

        return taux;
    }

    public long supprimerTaux(long idtaux) {
        return mDb.delete(nomtable, String.format("%s = ?", ID), new String[] {idtaux+""});
    }


    public List<String> getHormonesConcernees() {
        List<String> liste = new ArrayList<>();
        String requete = "SELECT "+HORMONE+" FROM "+nomtable+" GROUP BY "+HORMONE;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    liste.add(cursor.getString(cursor.getColumnIndex(HORMONE)));
                }
            }
            cursor.close();
        }
        return liste;
    }


    public long supprimerTousLesTaux() {
        return mDb.delete(nomtable, null, null);
    }


}
