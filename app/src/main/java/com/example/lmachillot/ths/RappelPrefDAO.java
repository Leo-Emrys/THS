package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmachillot on 23/03/17.
 */

public class RappelPrefDAO extends DAOBase {

    public static String nomtable = "rappelpref";
    public static String ID = "_id";
    public static String DELAI = "nbjours";
    public static String IDTRAITEMENT = "_id_traitement";
    public static String IDORDONNANCE = "_id_ordonnance";
    public static String IDSTOCK = "_id_stock";
    public static String HEURE = "heure";



    public RappelPrefDAO(Context pContext) {
        super(pContext);
    }

    public long ajouterRappelPref(RappelPref dp) {
        ContentValues values = new ContentValues();
        values.put(DELAI, dp.getDelai());
        if(dp.getTraitement()!=null)
            values.put(IDTRAITEMENT, dp.getTraitement().getId());
        else if (dp.getOrdonnance()!=null)
            values.put(IDORDONNANCE, dp.getOrdonnance().getId());
        else if(dp.getStock()!=null)
            values.put(IDSTOCK, dp.getStock().getId());
        values.put(HEURE, dp.getHeure());
        long id = mDb.insert(nomtable, null, values);
        dp.setId(id);
        return id;
    }

    public RappelPref getRappelPrefFrom(Traitement t, int delai, int heure) {
        RappelPref dp = null;
        String req = "SELECT * FROM "+nomtable+" WHERE "+IDTRAITEMENT+"="+t.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+" = "+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                dp = new RappelPref(id, delai, heure, t, null, null);
            }


            cursor.close();
        }

        return dp;
    }

    public RappelPref getRappelPrefFrom(Ordonnance o, int delai, int heure) {
        RappelPref dp = null;
        String req = "SELECT * FROM "+nomtable+" WHERE "+IDORDONNANCE+"="+o.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+" = "+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                dp = new RappelPref(id, delai, heure, null, o, null);
            }


            cursor.close();
        }

        return dp;
    }

    public RappelPref getRappelPrefFrom(Stock s, int delai, int heure) {
        RappelPref dp = null;
        String req = "SELECT * FROM "+nomtable+" WHERE "+IDSTOCK+"="+s.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+" = "+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                dp = new RappelPref(id, delai, heure, null, null, s);
            }


            cursor.close();
        }

        return dp;
    }

    public long supprimerRappelPref(RappelPref dp) {
        return mDb.delete(nomtable, String.format("%s = ?", ID), new String[] {dp.getId()+""});
    }


    public List<String> getRappelPrefStr(){
        List<String> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable;

        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    long iddp = cursor.getLong(0);
                    int delai = cursor.getInt(cursor.getColumnIndex(DELAI));
                    long idtraitement = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    int heure = cursor.getInt(cursor.getColumnIndex(HEURE));
                    long idordonnance = cursor.getLong(cursor.getColumnIndex(IDORDONNANCE));

                    liste.add(" ID : "+iddp+" / DELAI : "+delai+" / HEURE : "+heure+" / idtraitement : "+idtraitement+" / idordo : "+idordonnance);

                }
            }

            cursor.close();
        }

        return liste;
    }

    public List<RappelPref> getRappelPrefFrom(Traitement traitement) {
        List<RappelPref> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" WHERE "+IDTRAITEMENT+"="+traitement.getId();
        Cursor cursor = mDb.rawQuery(requete, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long iddp = cursor.getLong(0);
                    int delai = cursor.getInt(cursor.getColumnIndex(DELAI));
                    int heure = cursor.getInt(cursor.getColumnIndex(HEURE));

                    RappelPref dp = new RappelPref(iddp, delai, heure, traitement, null, null);
                    liste.add(dp);

                }
            }

            cursor.close();
        }

        return liste;
    }

    public List<RappelPref> getRappelPrefFrom(Ordonnance ordonnance) {
        List<RappelPref> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" WHERE "+IDORDONNANCE+"="+ordonnance.getId();
        Cursor cursor = mDb.rawQuery(requete, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long iddp = cursor.getLong(0);
                    int delai = cursor.getInt(cursor.getColumnIndex(DELAI));
                    int heure = cursor.getInt(cursor.getColumnIndex(HEURE));

                    RappelPref dp = new RappelPref(iddp, delai, heure, null, ordonnance, null);
                    liste.add(dp);

                }
            }

            cursor.close();
        }

        return liste;
    }

    public List<RappelPref> getRappelPrefFrom(Stock stock) {
        List<RappelPref> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" WHERE "+IDSTOCK+"="+stock.getId();
        Cursor cursor = mDb.rawQuery(requete, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long iddp = cursor.getLong(0);
                    int delai = cursor.getInt(cursor.getColumnIndex(DELAI));
                    int heure = cursor.getInt(cursor.getColumnIndex(HEURE));

                    RappelPref dp = new RappelPref(iddp, delai, heure, null, null, stock);
                    liste.add(dp);

                }
            }

            cursor.close();
        }

        return liste;
    }

}
