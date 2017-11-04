package com.example.lmachillot.ths;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lmachillot on 16/03/17.
 */

public class RappelDAO extends DAOBase {

    private Context pContext;

    public static String nomtable = "rappel";
    public static String ID = "_id";
    public static String DATE = "daterappel";
    public static String OBJET = "objet";
    public static String HEURE = "heure_rappel";
    public static String DELAI = "delai";
    public static String IDTRAITEMENT= "_id_traitement";
    public static String IDORDONNANCE = "_id_ordonnance";
    public static String IDSTOCK = "_id_stock";

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public RappelDAO(Context pContext) {
        super(pContext);
        this.pContext=pContext;

    }

    public long ajouterRappel(Rappel rappel) {
        java.sql.Date sqldate = new java.sql.Date(rappel.getDaterappel().getTime());

        ContentValues values = new ContentValues();
        values.put(OBJET, rappel.getObjet());
        values.put(DATE, sqldate.toString());
        values.put(HEURE, rappel.getHeure());
        values.put(DELAI, rappel.getDelai());
        values.put(IDTRAITEMENT, rappel.getIdtraitement());
        values.put(IDORDONNANCE, rappel.getIdordonnance());
        values.put(IDSTOCK, rappel.getIdstock());

        long id=mDb.insert(nomtable, null, values);
        if(id!=-1) {
            rappel.setId(id);
        }

        return id;
    }


    public long updateDateRappel(Rappel r) {
        java.sql.Date sqldate = new java.sql.Date(r.getDaterappel().getTime());
        ContentValues values = new ContentValues();
        values.put(DATE, sqldate.toString());

        long id = mDb.update(nomtable, values, String.format("%s = ?", ID), new String[]{r.getId()+""});


        return id;
    }

    public int getDelaiFrom(long idrappel) {
        int delai = -1;
        String req = "SELECT "+DELAI+" FROM "+nomtable+" WHERE "+ID+"="+idrappel;
        Cursor cursor = mDb.rawQuery(req, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                delai = cursor.getInt(cursor.getColumnIndex(DELAI));
            }

            cursor.close();
        }

        return delai;
    }

    public int getHeureFrom(long idrappel) {
        int heure = -1;
        String req = "SELECT "+HEURE+" FROM "+nomtable+" WHERE "+ID+"="+idrappel;
        Cursor cursor = mDb.rawQuery(req, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                heure = cursor.getInt(cursor.getColumnIndex(HEURE));
            }

            cursor.close();
        }

        return heure;
    }


    public boolean rappelExiste(Ordonnance o, int delai, int heure) {
        String req = "SELECT "+ID+" FROM "+nomtable+" WHERE "+IDORDONNANCE+"="+o.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+"="+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public boolean rappelExiste(Traitement t, int delai, int heure) {
        String req = "SELECT "+ID+" FROM "+nomtable+" WHERE "+IDTRAITEMENT+"="+t.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+"="+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean rappelExiste(Stock s, int delai, int heure) {
        String req = "SELECT "+ID+" FROM "+nomtable+" WHERE "+IDSTOCK+"="+s.getId()+" AND "+DELAI+"="+delai+" AND "+HEURE+"="+heure;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private List<Rappel> constrRappelFrom(Cursor cursor) {
        List<Rappel> liste = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id=cursor.getLong(0);
            String objet = cursor.getString(cursor.getColumnIndex(OBJET));
            String datestr = cursor.getString(cursor.getColumnIndex(DATE));
            int heure = cursor.getInt(cursor.getColumnIndex(HEURE));
            int delai = cursor.getInt(cursor.getColumnIndex(DELAI));
            long idtraitement = cursor.getLong(cursor.getColumnIndex(IDTRAITEMENT));
            long idordonnance = cursor.getLong(cursor.getColumnIndex(IDORDONNANCE));
            long idstock = cursor.getLong(cursor.getColumnIndex(IDSTOCK));

            format = new SimpleDateFormat("yyyy-MM-dd");
            Date date=null;

            try {
                date = format.parse(datestr);
            } catch (ParseException e) {
                Log.d("erreur format ", "premieredate traitementDAO //////////////////////////////////////");
                e.printStackTrace();
            }

            Rappel rappel = new Rappel(id, objet, date, heure, delai, idtraitement, idordonnance, idstock);
            liste.add(rappel);
        }

        return liste;
    }

    public List<Rappel> getRappels() {
        List<Rappel> liste = new ArrayList<>();
        String requete = "SELECT * FROM "+nomtable+" ORDER BY "+ID+" DESC LIMIT 50";

        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                liste = constrRappelFrom(cursor);
            }
            cursor.close();
        }

        return liste;
    }

    public long supprimerRappelsDuTraitement(long idtraitement) {
        //supprimmer alarmes associées
        List<Rappel> rappels = getRappelsFromIdTraitement(idtraitement);
        NotifManager notif = new NotifManager(pContext);
        for(Rappel r : rappels) {
            notif.supprimeralarme(r.getId());
        }
        //supprimer rappels dans la table
        return mDb.delete(nomtable, String.format("%s = ?", IDTRAITEMENT), new String[] {idtraitement+""});
    }

    public long supprimerRappelsDelOrdo(long idordo) {
        //supprimmer alarmes associées
        List<Rappel> rappels = getRappelsFromIdOrdo(idordo);
        NotifManager notif = new NotifManager(pContext);
        for(Rappel r : rappels) {
            notif.supprimeralarme(r.getId());
        }
        //supprimer rappels dans la table
        return mDb.delete(nomtable, String.format("%s = ?", IDORDONNANCE), new String[] {idordo+""});
    }

    public long supprimerRappelsDuStock(long idstock) {
        //supprimmer alarmes associées
        List<Rappel> rappels = getRappelsFromIdStock(idstock);
        NotifManager notif = new NotifManager(pContext);
        for(Rappel r : rappels) {
            notif.supprimeralarme(r.getId());
        }
        //supprimer rappels dans la table
        return mDb.delete(nomtable, String.format("%s = ?", IDSTOCK), new String[] {idstock+""});
    }

    public long supprimerRappelsDepasses() {
        long nblignes;
        Date dateactuelle = new Date();
        java.sql.Date sqldate = new java.sql.Date(dateactuelle.getTime());

        //supprimer rappels concernés
        nblignes=mDb.delete(nomtable,  String.format("%s < ?", DATE), new String[]{sqldate.toString()});

        return nblignes;
    }


    //supprimer un rappel
    public long supprimerRappelParId(long idrappel) {
        long suppr = mDb.delete(nomtable, String.format("%s = ?", ID),new String[]{idrappel+""});

        NotifManager notif = new NotifManager(pContext);
        notif.supprimeralarme(idrappel);

        return suppr;
    }



    //récupérer les rappels traitement du traitement t
    public List<Rappel> getRappelsFrom(Traitement t) {
        List<Rappel> liste = new ArrayList<>();
        String requete = "SELECT * FROM "+nomtable+
                         " WHERE "+IDTRAITEMENT+"="+t.getId()+" AND "+OBJET+"='traitement'";
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                liste = constrRappelFrom(cursor);
            }
            cursor.close();
        }


        return liste;
    }

    public Rappel getRappel(long idrappel) {
        Rappel rappel = null;
        String req = "SELECT * FROM "+nomtable+" WHERE "+ID+"="+idrappel;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                rappel=constrRappelFrom(cursor).get(0);
            }

            cursor.close();
        }
        return rappel;
    }

    public List<Rappel> getRappelsFromIdTraitement(long idtraitement) {
        List<Rappel> liste = new ArrayList<>();

        String req = "SELECT * FROM "+nomtable+" WHERE "+IDTRAITEMENT+" = "+idtraitement;
        Cursor cursor = mDb.rawQuery(req, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                liste = constrRappelFrom(cursor);
            }

            cursor.close();
        }

        return liste;
    }

    public List<Rappel> getRappelsFromIdOrdo(long idordo) {
        List<Rappel> liste = new ArrayList<>();

        String req = "SELECT * FROM "+nomtable+" WHERE "+IDORDONNANCE+" = "+idordo;
        Cursor cursor = mDb.rawQuery(req, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                liste = constrRappelFrom(cursor);
            }

            cursor.close();
        }

        return liste;

    }

    public List<Rappel> getRappelsFromIdStock(long idstock) {
        List<Rappel> liste = new ArrayList<>();

        String req = "SELECT * FROM "+nomtable+" WHERE "+IDSTOCK+" = "+idstock;
        Cursor cursor = mDb.rawQuery(req, null);

        if (cursor!=null) {
            if (cursor.getCount()>0) {
                liste = constrRappelFrom(cursor);
            }

            cursor.close();
        }

        return liste;

    }


    public Rappel getProchainRappelTraitement(Traitement t) {
        Rappel rappel = null;

        String requete = "SELECT * FROM "+nomtable+"  WHERE "+IDTRAITEMENT+"="+t.getId()+
                        " ORDER BY "+DATE+" ASC LIMIT 1 ";
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                rappel=constrRappelFrom(cursor).get(0);
            }


            cursor.close();
        }


        return rappel;

    }

    public Rappel getProchainRappelOrdo(Ordonnance o) {
        Rappel rappel = null;
        String requete = "SELECT * FROM "+nomtable+"  WHERE "+IDORDONNANCE+"="+o.getId()+
                " ORDER BY "+DATE+" ASC LIMIT 1 ";
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                rappel=constrRappelFrom(cursor).get(0);
            }

            cursor.close();
        }

        return rappel;
    }

    public Rappel getProchainRappelStock(Stock s) {
        Rappel rappel = null;
        String requete = "SELECT * FROM "+nomtable+" WHERE "+IDSTOCK+"="+s.getId()+
                            " ORDER BY "+DATE+" ASC LIMIT 1";
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                rappel=constrRappelFrom(cursor).get(0);
            }

            cursor.close();
        }

        return rappel;
    }
}
