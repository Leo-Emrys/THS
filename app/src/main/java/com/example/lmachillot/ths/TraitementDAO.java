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
 * Created by lmachillot on 13/03/17.
 */

public class TraitementDAO extends DAOBase {

    public static String nomtable = "traitement";

    public static String ID = "_id";
    public static String NOM = "nomtraitement";
    public static String HORMONE = "hormone";
    public static String FREQUENCE = "frequence";
    public static String PREMIERE_PRISE = "premiere_prise";
    public static String DATE_RENOUVELLEMENT = "date_renouvellement";
    public static String STOCK = "_id_stock";
    public static String TYPE = "_id_type";
    public static String DOSAGE = "dosage";

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public TraitementDAO(Context pContext) {
        super(pContext);


    }

    public long ajouterTraitement(Traitement t) {

        // récupérer clé du type voulu
        String req = "SELECT _id FROM type WHERE intituletype = '"+t.getType().name()+"'";
        Cursor cursor = mDb.rawQuery(req, null);

        cursor.moveToFirst();
        int idtype = cursor.getInt(0);

        cursor.close();

        //insertion
        ContentValues value = new ContentValues();
        value.put(NOM, t.getNom());
        value.put(HORMONE, t.getHormone().toString());
        value.put(FREQUENCE, t.getFrequence());
        value.put(DOSAGE, t.getDosage());
        value.put(TYPE, idtype);

        if(t.getStock()!=null) {
            value.put(STOCK, t.getStock().getId());
        }

        //conversion des dates en format sql si t contient déjà les dates, et insertion des dates
        if(t.getPremiereprise()!=null && t.getDate_renouvellement()!=null) {
            java.sql.Date sqlDatePrem = new java.sql.Date(t.getPremiereprise().getTime());
            //Log.d("sqldate première prise", " ////////////////////////////// "+sqlDatePrem+"");

            java.sql.Date sqlDateSuiv = new java.sql.Date(t.getDate_renouvellement().getTime());
            //Log.d("sqldate prochaine prise", " ////////////////////////////// "+sqlDateSuiv+"");

            value.put(PREMIERE_PRISE, sqlDatePrem.toString());
            value.put(DATE_RENOUVELLEMENT, sqlDateSuiv.toString());
        }

        //Log.d("value content", " :::::::::::::::::::::::::::::::"+value+"");


        long id = mDb.insert(nomtable, null, value);

        //mettre ajout id du Traitement dans modèle
        t.setId(id);

        return id;
    }

    public int setDatePremierePrise(Traitement t) {
        java.sql.Date sqldate = new java.sql.Date(t.getPremiereprise().getTime());

        ContentValues args = new ContentValues();
        args.put(PREMIERE_PRISE, sqldate.toString());

        int nblignes = mDb.update(nomtable, args, String.format("%s = ?", ID), new String[]{t.getId()+""});

        return nblignes;
    }

    public Date getProchaineDate(long idtraitement) {
        Date prochainedate = null;

        String requete = "SELECT "+DATE_RENOUVELLEMENT+" FROM "+nomtable+" WHERE "+ID+"="+idtraitement;
        Cursor cursor=mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String datestr = cursor.getString(cursor.getColumnIndex(DATE_RENOUVELLEMENT));

                if(datestr!=null) {
                    try {
                        prochainedate = format.parse(datestr);
                    } catch (ParseException e) {
                        Log.d("erreur format ", "get prochaine date traitementDAO ///////////////////////////////////");
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d("---------------", "Prochaine date non trouvée pour le traitement (id="+idtraitement+")");
            }
            cursor.close();
        }

        return prochainedate;
    }


    public int majDateRenouvellement(Traitement t) {
        //conversion date
        java.sql.Date sqldaterenouv = new java.sql.Date(t.getDate_renouvellement().getTime());

        ContentValues args = new ContentValues();
        args.put(DATE_RENOUVELLEMENT, sqldaterenouv.toString());

        int ligneschangées = mDb.update(nomtable, args, String.format("%s = ?", ID),
                new String[]{t.getId()+""});

        return ligneschangées;
       // String req = "UPDATE "+nomtable+" SET "+DATE_RENOUVELLEMENT+"='"+t.getDate_renouvellement().toString()+"' WHERE "+ID+"="+t.getId();
        //mDb.execSQL(req);
    }


    private List<Traitement> creerTraitementsDepuisBD(Cursor cursor) {
        List<Traitement> liste = new ArrayList<>();

        while(cursor.moveToNext()) {
            //récupérer type
            String typestr = cursor.getString(cursor.getColumnIndex("intituletype"));
            Type type=null;
            for(Type t : Type.values()) {
                if(t.name().equals(typestr)) {
                    type=t;
                }
            }
            if(type==null) {
                Log.d("------------------", "type inconnu !!");
            }

            //récupérer id

            Long idtraitement=cursor.getLong(0);
            //nom
            String nom = cursor.getString(cursor.getColumnIndex(NOM));
            //hormone
            String hormonestr = cursor.getString(cursor.getColumnIndex(HORMONE));
            Hormone hormone=null;
            for(Hormone h : Hormone.values()) {
                if (h.name().equals(hormonestr)) {
                    hormone = h;
                }
            }

            //frequence
            int frequence = cursor.getInt(cursor.getColumnIndex(FREQUENCE));

            //première date
            String premieredatestr = cursor.getString(cursor.getColumnIndex(PREMIERE_PRISE));

            //Log.d("''''''''''''''''''première date dans BD =============", "?"+premieredatestr);


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date premieredate=null;

            if(premieredatestr!=null) {
                try {
                    premieredate = format.parse(premieredatestr);
                } catch (ParseException e) {
                    Log.d("erreur format ", "premieredate traitementDAO //////////////////////////////////////");
                    e.printStackTrace();
                }
            }


            //date renouvellement
            String daterenouvstr = cursor.getString(cursor.getColumnIndex(DATE_RENOUVELLEMENT));
            Date daterenouv=null;

            //Log.d("''''''''''''''''''date renouv dans BD =============", daterenouvstr);

            if(daterenouvstr!=null) {
                try {
                    daterenouv = format.parse(daterenouvstr);
                } catch (ParseException e) {
                    Log.d("erreur format  ", "daterenouv traitementDAO //////////////////////////////////////");
                    e.printStackTrace();
                }
            }


            //stock
            long stockid = cursor.getLong(cursor.getColumnIndex(STOCK));
            Stock stock = null;
            if(stockid>0) {
                String reqstock = "SELECT * FROM "+StockDAO.nomtable+" WHERE "+StockDAO.ID+"="+stockid;
                Cursor c2 = mDb.rawQuery(reqstock, null);
                if(c2!=null) {
                    if(c2.getCount()>0) {
                        String datestockstr = c2.getString(cursor.getColumnIndex(StockDAO.DATE));
                        int duree = c2.getInt(cursor.getColumnIndex(StockDAO.DUREE));

                        Date datestock=null;
                        try {
                            datestock=format.parse(datestockstr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d("ERREUR FORMAT /////", "////////////// datetock TraitementDAO");
                        }

                        stock = new Stock(stockid, datestock, duree);

                    }
                    c2.close();
                }
            }

            String dosage = cursor.getString(cursor.getColumnIndex(DOSAGE));




            Traitement trtmt = new Traitement(idtraitement, nom, hormone, frequence, premieredate, daterenouv, stock, dosage, type);
            liste.add(trtmt);

        }
        return liste;
    }

    public Traitement getTraitementParId(long id) {
        Traitement traitement = null;

        String requete = "SELECT traitement."+ID+", "+NOM+", "+HORMONE+", "+FREQUENCE+", "+PREMIERE_PRISE+", "+DATE_RENOUVELLEMENT+", "+STOCK+", "+DOSAGE+", "
                +"type._id, "+TypeDAO.INTITULE+", "+TypeDAO.DENOMINATION +
                " FROM "+nomtable+" INNER JOIN "+TypeDAO.nomtable+
                " ON "+TYPE+"=type._id"+
                " WHERE "+nomtable+"."+ID+"="+id;

        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                List<Traitement> liste = creerTraitementsDepuisBD(cursor);
                if(liste.size()>0) {
                    traitement=liste.get(0);
                } else {
                    Log.d("------------------", "problème pour récupérer traitement par id");
                }
            } else {
                Log.d("*************", "pas de traitement pour l'id"+id);
            }
            cursor.close();

        } else {
            Log.d("-----------------", "cursor null get traitement par id");

        }


        return traitement;
    }


    //récupérer id à partir du nom. on suppose nom unique --> contrainte faite dans nouveau traitement
    public long getIdTraitementParNom(String nom) {
        long idt = -1;

        String requete = "SELECT "+ID+" FROM "+nomtable+" WHERE "+NOM+"='"+nom+"'";
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                idt = cursor.getLong(0);
            }

            cursor.close();
        }

        return idt;
    }


    public List<Traitement> getTraitements() {
        List<Traitement> liste = new ArrayList<>();

        String requete = "SELECT traitement."+ID+", "+NOM+", "+HORMONE+", "+FREQUENCE+", "+PREMIERE_PRISE+", "+DATE_RENOUVELLEMENT+", "+STOCK+", "+DOSAGE+", "
                                    +"type._id, intituletype, denomination "+
                        "FROM traitement INNER JOIN type ON "+TYPE+"=type._id"+
                        " ORDER BY "+DATE_RENOUVELLEMENT+" ASC";

        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                liste=creerTraitementsDepuisBD(cursor);
            } else {
                Log.d("----", "+++++++++++++++++++++++++pas de lignes ?????????????????");
            }
            cursor.close();
        } else {
            Log.d("--------", "----------------problème");
        }


        return liste;
    }

    // récupérer Id dernière entrée
    public long getIdDernierTraitement() {
        long id = -1;
        String requete = "SELECT "+ID+" FROM "+nomtable;
        Cursor cursor = mDb.rawQuery(requete, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToLast();
                id = cursor.getLong(0);
            }
            cursor.close();
        }


        return id;
    }

    public String getNomTraitement(long idtraitement) {
        String nom=null;
        String requete = "SELECT "+NOM+" FROM "+nomtable+" WHERE "+ID+"="+idtraitement;
        Cursor cursor = mDb.rawQuery(requete, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                nom=cursor.getString(cursor.getColumnIndex(NOM));
            }
            cursor.close();
        }
        return nom;
    }


    public boolean traitementPresent() {
        String req = "SELECT "+ID+" FROM "+nomtable+" LIMIT 1";
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {

            Log.d("++++++++++++++++++++++", "cursor count "+cursor.getCount());

            if(cursor.getCount()>0) {
                cursor.moveToFirst();

                Log.d("++++++++++++????", cursor.getLong(0)+" /nom :");
                return true;
            }

            cursor.close();
        }

        return false;
    }


    //mise à jour traitement
    public long updateTraitement(Traitement t) {
       ContentValues values = new ContentValues();
        values.put(NOM, t.getNom());
        values.put(HORMONE, t.getHormone().toString());
        values.put(FREQUENCE, t.getFrequence()+"");
        values.put(DOSAGE, t.getDosage()+"");

        //recup idtype
        Type type = Type.valueOf(t.getType().name());
        long idtype = type.ordinal()+1;

        values.put(TYPE, idtype);

        long modif = mDb.update(nomtable, values, String.format("%s = ?", ID), new String[] {t.getId()+""});

        return modif;
    }


    //supprimer un traitement
    public long supprimerTraitement(Long idt) {

        //suppr ordo associées

        List<Long> idordos = new ArrayList<>();
        String req = "SELECT "+TC_OrdoTraitementDAO.IDORDONNANCE+" FROM "+TC_OrdoTraitementDAO.nomtable+" WHERE "+TC_OrdoTraitementDAO.IDTRAITEMENT+"="+idt;
        Cursor cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    idordos.add(cursor.getLong(cursor.getColumnIndex(TC_OrdoTraitementDAO.IDORDONNANCE)));
                }
            }

            cursor.close();
        }

        mDb.delete(TC_OrdoTraitementDAO.nomtable, String.format("%s = ?", TC_OrdoTraitementDAO.IDTRAITEMENT), new String[] {idt+""});

        for(Long id : idordos) {
            //supprimer rappels des ordonnances
            long s = mDb.delete(RappelDAO.nomtable, String.format("%s = ?", RappelDAO.IDORDONNANCE), new String[] {id+""});
            Log.d("uuuuuuuuuuuuuuuuuuuuuuu", s+"rappels supprimés pour ordo "+id);
            //supprimer préférences
            long supp = mDb.delete(RappelPrefDAO.nomtable, String.format("%s = ?", RappelPrefDAO.IDORDONNANCE), new String[] {id+""});
            Log.d("uuuuuuuuuuuuuuuuuuuuuuu", supp+"rappels pref supprimés pour ordo "+id);
            //supprimer ordonnances
            mDb.delete(OrdonnanceDAO.nomtable, String.format("%s = ?", OrdonnanceDAO.ID), new String[] {id+""});
        }

        //suppr stocks associés SI seulement pour ce traitement
        List<Long> idstocks = new ArrayList<>();
        req = "SELECT "+TC_StockTraitementDAO.IDSTOCK+" FROM "+TC_StockTraitementDAO.nomtable+" WHERE "+TC_StockTraitementDAO.IDTRAITEMENT+"="+idt;
        cursor = mDb.rawQuery(req, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                while(cursor.moveToNext()) {
                    idstocks.add(cursor.getLong(cursor.getColumnIndex(TC_StockTraitementDAO.IDSTOCK)));
                }
            }

            cursor.close();
        }

        mDb.delete(TC_StockTraitementDAO.nomtable, String.format("%s = ?", TC_StockTraitementDAO.IDTRAITEMENT), new String[] {idt+""});

        for(long id : idstocks) {
            //verif si idstock encore dans table TC -> assoc à un autre traitement car on a suppr celui là
            req = "SELECT * FROM "+TC_StockTraitementDAO.nomtable+" WHERE "+TC_StockTraitementDAO.IDSTOCK+"="+id;
            cursor = mDb.rawQuery(req, null);
            if(cursor==null || cursor.getCount()<=0) { // pas d'autre traitement assoc
                //supprimer rappels de stock
                long s = mDb.delete(RappelDAO.nomtable, String.format("%s = ?", RappelDAO.IDSTOCK), new String[] {id+""});
                Log.d("uuuuuuuuuuuuuuuuuuuuuuu", s+" rappels supprimés pour stock "+id);
                //supprimer préférences
                long supp = mDb.delete(RappelPrefDAO.nomtable, String.format("%s = ?", RappelPrefDAO.IDSTOCK), new String[] {id+""});
                Log.d("uuuuuuuuuuuuuuuuuuuuuuu", supp+" rappels pref supprimés pour stock "+id);
                //supprimer stock
                mDb.delete(StockDAO.nomtable, String.format("%s = ?", StockDAO.ID), new String[] {id+""});
            }
        }


        //suppr rappels associés
        mDb.delete(RappelDAO.nomtable, String.format("%s = ?", RappelDAO.IDTRAITEMENT), new String[] {idt+""});

        //suppr rappels pref associés
        mDb.delete(RappelPrefDAO.nomtable, String.format("%s = ?", RappelPrefDAO.IDTRAITEMENT), new String[] {idt+""});


        //suppr prises associées
        mDb.delete(PriseDAO.nomtable, String.format("%s = ?", PriseDAO.IDTRAITEMENT), new String[] {idt+""});

        //suppr lignes tc traitement zone associées
        mDb.delete(TC_TraitementZoneDAO.nomtable, String.format("%s = ?", TC_TraitementZoneDAO.IDTRAITEMENT), new String[] {idt+""});

        //suppression Traitement proprement dit
        return mDb.delete(nomtable, String.format("%s = ?", ID), new String[] {idt+""});

    }



    //supprimer toutes les données de la table  !!!!!!!!!!!!! clé étrangére dans d'autres tables
    public long supprimerTousTraitements() {
        mDb.delete("ordonnance", null, null);
        mDb.delete(RappelDAO.nomtable, null, null);
        mDb.delete(RappelPrefDAO.nomtable, null, null);
        mDb.delete(PriseDAO.nomtable, null, null);
        mDb.delete(TC_TraitementZoneDAO.nomtable, null, null);
        return mDb.delete(nomtable, null, null);
    }


}
