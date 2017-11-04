package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lmachillot on 16/03/17.
 */

public class PriseDAO extends DAOBase {

    public static String nomtable = "prise";
    public static String ID = "_id";
    public static String  DATE = "dateprise";
    public static String IDTRAITEMENT = "_id_traitement";
    public static String IDZONE = "_id_zone";

    public PriseDAO(Context pContext) {
        super(pContext);
    }


    public long ajouterPrise(Prise p) {

        long idzone;
        //verif s'il y a zone
        if(p.getZone()==null) {
            idzone=-1;
        } else {
            idzone=p.getZone().getId();
        }

        ContentValues values = new ContentValues();
        values.put(IDTRAITEMENT, p.getTraitement().getId());
        values.put(IDZONE, idzone);

        java.sql.Date sqldate = new java.sql.Date(p.getDateprise().getTime());

        values.put(DATE, sqldate.toString());

        long idinsert = mDb.insert(nomtable, null, values);
        //renvoie -1 si pas réussi, id de la ligne insérée sinon

        if(idinsert!=-1)
            p.setId(idinsert);

        return idinsert;

    }

    //DEBUG
    public List<String> getPrisesStr() {
        List<String> liste = new ArrayList<>();

        String requete = "SELECT * FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    Long id = cursor.getLong(0);
                    Long idtraitement = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
                    Long idzone = cursor.getLong(cursor.getColumnIndex(IDZONE));
                    String datestr = cursor.getString(cursor.getColumnIndex(DATE));

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date=null;

                    try {
                        date = format.parse(datestr);
                    } catch (ParseException e) {
                        Log.e("erreur format premieredate traitementDAO", "//////////////////////////////////////");
                        e.printStackTrace();
                    }
                    liste.add("id : "+id+" / date : "+date+" / idtraitement : "+idtraitement+" / idzone : "+idzone);

                }
            }
            cursor.close();
        }

        return liste;
    }


    public List<Prise> getPrisesFrom(Traitement t) {
        List<Prise> liste = new ArrayList<>();

        String req = "SELECT * FROM "+nomtable+" WHERE _id_traitement="+t.getId();
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    long idprise = cursor.getLong(0);
                    String datestr = cursor.getString(cursor.getColumnIndex(DATE));
                    long idzone = cursor.getLong(cursor.getColumnIndex(IDZONE));

                    //recup zone associée
                    Zone zone = zoneAssoc(idzone);

                    //conversion date
                    Date dateprise=null;
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    if(datestr!=null) {
                        try {
                            dateprise = format.parse(datestr);
                        } catch (ParseException e) {
                            Log.e("erreur format get prochaine date traitementDAO", "///////////////////////////////////");
                            e.printStackTrace();
                        }
                    }

                    Prise prise = new Prise(idprise, dateprise, t, zone);
                    liste.add(prise);
                }


            } else {
                Log.d("-------------------", "pas de zones associées (PriseDAO)");
            }
            cursor.close();
        }

        return liste;
    }

    public Prise dernierePriseFrom(Traitement t) {
        Prise prise = null;
        String req = "SELECT max("+ID+"), "+DATE+", "+IDZONE+" FROM "+nomtable+" WHERE _id_traitement="+t.getId();
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                long idprise = cursor.getLong(0);
                String datestr = cursor.getString(cursor.getColumnIndex(DATE));
                long idzone = cursor.getLong(cursor.getColumnIndex(IDZONE));

                //recup zone associée
                Zone zone = zoneAssoc(idzone);

                //conversion date
                Date dateprise=null;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                if(datestr!=null) {
                    try {
                        dateprise = format.parse(datestr);
                    } catch (ParseException e) {
                        Log.d("erreur format get prochaine date traitementDAO", "///////////////////////////////////");
                        e.printStackTrace();
                    }
                }

                prise = new Prise(idprise, dateprise, t, zone);

            } else {
                Log.d("-------------------", "pas de zones associées (PriseDAO)");
            }
            cursor.close();
        }
        return prise;
    }

    private Zone zoneAssoc(long idzone) {
        Zone zone = null;
        String req = "SELECT * FROM zone WHERE _id="+idzone;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                String intitule = cursor.getString(cursor.getColumnIndex(ZoneDAO.INTITULE));
                String cote = cursor.getString(cursor.getColumnIndex(ZoneDAO.COTE));
                zone = new Zone(idzone, intitule, cote);
            }
            cursor.close();
        }


        return zone;
    }


}
