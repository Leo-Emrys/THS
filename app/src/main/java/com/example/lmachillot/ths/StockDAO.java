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

public class StockDAO extends DAOBase {

    public static String nomtable = "stock";
    public static String ID = "_id";
    public static String DATE = "datestock";
    public static String DUREE = "duree_stock";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public StockDAO(Context pContext) {
        super(pContext);
    }

    public long ajouterStock(Stock s) {
        ContentValues values = new ContentValues();
        values.put(DUREE, s.getDuree());

        java.sql.Date datesql = new java.sql.Date(s.getDatestock().getTime());

        values.put(DATE, datesql.toString());

        long id = mDb.insert(nomtable, null, values);

        s.setId(id);

        return id;
    }

    private List<Stock> constrStockFrom(Cursor cursor) {
        List<Stock> liste = new ArrayList<>();

        while (cursor.moveToNext()) {

            long id = cursor.getLong(0);
            String datestr = cursor.getString(cursor.getColumnIndex(DATE));
            int duree = cursor.getInt(cursor.getColumnIndex(DUREE));

            Date date = null;

            try {
                date = format.parse(datestr);
            } catch (ParseException e) {
                Log.d("////////////////", "erreur format date stock DAO");
                e.printStackTrace();
            }

            liste.add(new Stock(id, date, duree));
        }

        return liste;
    }

    public List<Stock> getStocks() {
        List<Stock> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable+" ORDER BY "+DATE+" ASC";
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                liste = constrStockFrom(cursor);

            }

            cursor.close();
        }

        return liste;
    }


    public Stock getStock(long idstock) {
        Stock stock = null;

        String req = "SELECT * FROM "+nomtable+" WHERE "+ID+"="+idstock;
        Cursor cursor = mDb.rawQuery(req, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                stock = constrStockFrom(cursor).get(0);
            }

            cursor.close();
        }

        return stock;
    }

    public long supprimerStock(long idstock) {
        //supprimer ref TC
        mDb.delete(TC_StockTraitementDAO.nomtable, String.format("%s = ?", TC_StockTraitementDAO.IDSTOCK), new String[] {idstock+""});

        //supprimer rappel
        mDb.delete(RappelDAO.nomtable, String.format("%s = ?", RappelDAO.IDSTOCK), new String[] {idstock+""});

        //supprimer rappel pref
        mDb.delete(RappelPrefDAO.nomtable, String.format("%s = ?", RappelDAO.IDSTOCK), new String[] {idstock+""});

        //supprimer stock
        return mDb.delete(nomtable, String.format("%s = ?", ID), new String[] {idstock+""});
    }

    public long updateStock(Stock stock) {
        java.sql.Date sqldate = new java.sql.Date(stock.getDatestock().getTime());

        ContentValues values = new ContentValues();
        values.put(DUREE, stock.getDuree());
        values.put(DATE, sqldate.toString());

        return mDb.update(nomtable, values, String.format("%s=?", ID), new String[] {stock.getId()+""});
    }
}
