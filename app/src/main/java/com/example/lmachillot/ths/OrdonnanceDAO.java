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

public class OrdonnanceDAO extends DAOBase {

    public static String nomtable = "ordonnance";
    public static String ID = "_id";
    public static String NOM = "nom_ordo";
    public static String DATE = "date_ordo";
    public static String DUREE = "duree_ordo";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public OrdonnanceDAO(Context pContext) {
        super(pContext);
    }


    public long ajouterOrdonnance(Ordonnance o) {
        ContentValues values = new ContentValues();
        values.put(NOM, o.getNom());
        values.put(DUREE, o.getDuree());

        java.sql.Date datesql = new java.sql.Date(o.getDateordo().getTime());

        values.put(DATE, datesql.toString());

        long id = mDb.insert(nomtable, null, values);

        o.setId(id);

        return id;
    }


    private List<Ordonnance> constrOrdonnanceFrom(Cursor cursor) {
        List<Ordonnance> liste = new ArrayList<>();

        while (cursor.moveToNext()) {

            long id = cursor.getLong(0);
            String nom = cursor.getString(cursor.getColumnIndex(NOM));
            String datestr = cursor.getString(cursor.getColumnIndex(DATE));
            int duree = cursor.getInt(cursor.getColumnIndex(DUREE));

            Date date = null;

            try {
                date = format.parse(datestr);
            } catch (ParseException e) {
                Log.d("///////////////////", "erreur format date ordo DAO");
                e.printStackTrace();
            }

            liste.add(new Ordonnance(id, nom, date, duree));
        }

        return liste;
    }


    public List<Ordonnance> getOrdonnances() {
        List<Ordonnance> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                    liste = constrOrdonnanceFrom(cursor);

            }

            cursor.close();
        }

        return liste;
    }


    public Ordonnance getOrdonnance(long idordo) {
        Ordonnance ordonnance = null;

        String req = "SELECT * FROM "+nomtable+" WHERE "+ID+"="+idordo;
        Cursor cursor = mDb.rawQuery(req, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                ordonnance = constrOrdonnanceFrom(cursor).get(0);
            }

            cursor.close();
        }

        return ordonnance;
    }

    public long updateOrdonnance(Ordonnance o) {
        java.sql.Date sqldate = new java.sql.Date(o.getDateordo().getTime());

        ContentValues values = new ContentValues();
        values.put(DUREE, o.getDuree());
        values.put(DATE, sqldate.toString());
        values.put(NOM, o.getNom());

        return mDb.update(nomtable, values, String.format("%s=?", ID), new String[] {o.getId()+""});

    }


    public long supprimerOrdonnance(long idordo) {

        //supprimer ref TC
        mDb.delete(TC_OrdoTraitementDAO.nomtable, String.format("%s = ?", TC_OrdoTraitementDAO.IDORDONNANCE), new String[] {idordo+""});

        //supprimer rappel
        mDb.delete(RappelDAO.nomtable, String.format("%s = ?", RappelDAO.IDORDONNANCE), new String[] {idordo+""});

        //supprimer rappel pref
        mDb.delete(RappelPrefDAO.nomtable, String.format("%s = ?", RappelDAO.IDORDONNANCE), new String[] {idordo+""});

        //supprimer ordo
        return mDb.delete(nomtable, String.format("%s = ?", ID), new String[] {idordo+""});

    }


}
