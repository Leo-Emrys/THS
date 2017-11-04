package com.example.lmachillot.ths;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by lmachillot on 13/03/17.
 */

public class TypeDAO extends DAOBase {

    public static String nomtable = "type";

    public static String ID = "_id";
    public static String INTITULE = "intituletype";
    public static String DENOMINATION = "denomination";


    public TypeDAO(Context pContext) {
        super(pContext);
    }

    public boolean ajouterType(Type t) {

        //String requete = "INSERT INTO "+nomtable+" VALUES (null, "+"essai"+", "+"bonjour"+")";
        String requete = "INSERT INTO "+nomtable+" VALUES (null, '"+t.name().toString()+"',  '"+t.getDenom().toString()+"')";


        mDb.execSQL(requete);


        return true;
    }

    public long getIdDuType(String intitule) {
        String req = "SELECT "+ ID+" FROM "+ nomtable+" WHERE "+INTITULE+" = "+intitule;
        Cursor cursor = mDb.rawQuery(req, null);
        cursor.moveToFirst();
        long idtype = cursor.getLong(0);

        cursor.close();

        return idtype;
    }
}
