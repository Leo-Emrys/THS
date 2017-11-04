package com.example.lmachillot.ths;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lmachillot on 13/03/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "ths.db";
    private static final int DATABASE_VERSION = 1;


    //CRÉATION TABLES

    public static final String TYPE_TABLE_CREATE =
            "CREATE TABLE type(" +
                    "_id           INTEGER PRIMARY KEY ," +
                    "intituletype  TEXT ," +
                    "denomination  TEXT," +
                    "accord        TEXT);";

    public static final String STOCK_TABLE_CREATE =
            "CREATE TABLE stock(" +
                    "_id          INTEGER PRIMARY KEY , " +
                    "datestock    TEXT , " +
                    "duree_stock  INTEGER " +
                    ");";

    public static final String TRAITEMENT_TABLE_CREATE =

            "CREATE TABLE traitement("+
                    "_id                  INTEGER PRIMARY KEY ,"+
                    "nomtraitement        TEXT ,"+
                    "hormone              TEXT ,"+
                    "frequence            INTEGER ,"+
                    "premiere_prise       TEXT ,"+
                    "date_renouvellement  TEXT ,"+
                    "_id_stock          INTEGER ,"+
                    "_id_type             INTEGER ,"+
                    "dosage               REAL ,"+
                    "FOREIGN KEY (_id_stock) REFERENCES stock(_id) , "+
                    "FOREIGN KEY (_id_type) REFERENCES type(_id) );";

    public static final String TC_STOCK_TRAITEMENT_TABLE_CREATE =
            "CREATE TABLE tc_stock_traitement(" +
                    "_id_traitement  INTEGER NOT NULL , " +
                    "_id_stock  INTEGER NOT NULL , " +
                    "PRIMARY KEY (_id_traitement, _id_stock) , " +
                    "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id)," +
                    "FOREIGN KEY (_id_stock) REFERENCES stock(_id)" +
                    ");";


    public static final String PRISE_TABLE_CREATE =
            "CREATE TABLE prise(" +
                    "_id             INTEGER PRIMARY KEY ," +
                    "dateprise       TEXT NOT NULL ," +
                    "_id_traitement  INTEGER ," +
                    "_id_zone        INTEGER ,"+
                    "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id)" +
                    "FOREIGN KEY (_id_zone) REFERENCES zone(_id)" +
                    ");";

    public static final String RAPPELPREF_TABLE_CREATE =
             "CREATE TABLE rappelpref(" +
                     "_id             INTEGER PRIMARY KEY , " +
                     "nbjours         INTEGER , " +
                     "_id_traitement  INTEGER , " +
                     "heure           INTEGER , " +
                     "_id_ordonnance  INTEGER , " +
                     "_id_stock INTEGER , " +
                     "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id) ," +
                     "FOREIGN KEY (_id_ordonnance) REFERENCES ordonnance(_id)"+
                     "FOREIGN KEY (_id_stock) REFERENCES stock(_id)"+
                     ");";


    public static final String ZONE_TABLE_CREATE =
            "CREATE TABLE zone(" +
                    "_id       INTEGER PRIMARY KEY ," +
                    "intitule  TEXT ," +
                    "cote      TEXT );";

    public static final String TC_TRAITEMENT_ZONE_TABLE_CREATE =
            "CREATE TABLE tc_traitement_zone(" +
                    "_id_traitement        INTEGER NOT NULL ," +
                    "_id_zone   INTEGER NOT NULL ," +
                    "frequence  REAL ," +
                    "PRIMARY KEY (_id_traitement,_id_zone) ," +
                    "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id)," +
                    "FOREIGN KEY (_id_zone) REFERENCES zone(_id)" +
                    ");";

    public static final String RAPPEL_TABLE_CREATE =
            "CREATE TABLE rappel(" +
                    "_id         INTEGER PRIMARY KEY, " +
                    "objet        TEXT, " +
                    "daterappel  TEXT, " +
                    "heure_rappel INTEGER, "+
                    "delai  INTEGER, " +
                    "_id_traitement  INTEGER, "+
                    "_id_ordonnance INTEGER , "+
                    "_id_stock INTEGER , " +
                    "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id), "+
                    "FOREIGN KEY (_id_ordonnance) REFERENCES ordonnance(_id)"+
                    "FOREIGN KEY (_id_stock) REFERENCES stock(_id)"+
                    ");";


    public static final String ORDONNANCE_TABLE_CREATE =
            "CREATE TABLE ordonnance(" +
                    "_id         INTEGER PRIMARY KEY ," +
                    "nom_ordo    TEXT ,"+
                    "date_ordo   TEXT ," +
                    "duree_ordo  INTEGER "+
                    ");";

    public static final String TC_ORDO_TRAITEMENT_CREATE =
            "CREATE TABLE tc_ordo_traitement(" +
                    "_id_traitement  INTEGER NOT NULL , " +
                    "_id_ordonnance  INTEGER NOT NULL , " +
                    "PRIMARY KEY (_id_traitement, _id_ordonnance) , " +
                    "FOREIGN KEY (_id_traitement) REFERENCES traitement(_id)," +
                    "FOREIGN KEY (_id_ordonnance) REFERENCES ordonnance(_id)" +
                    ");";


    public static final String TAUXHORMONAL_TABLE_CREATE =
            "CREATE TABLE tauxhormonal(" +
                    "        _id         INTEGER PRIMARY KEY ," +
                    "        hormone     Varchar (25) ," +
                    "        dateanalyse Varchar (25) ," +
                    "        taux        REAL" +
                    ");";

    public static final String ALARME_TABLE_CREATE =
            "CREATE TABLE alarme(" +
                    "_id         INTEGER PRIMARY KEY , " +
                    "datealarme  TEXT , " +
                    "idalarme INTEGER " +
                    ");";

    //SUPPRESSION TABLES

    public static final String TC_ORDO_TRAITEMENT_TABLE_DROP = "DROP TABLE IF EXISTS tc_ordo_traitement;";
    public static final String ORDONNANCE_TABLE_DROP = "DROP TABLE IF EXISTS ordonnance;";
    public static final String RAPPEL_TABLE_DROP = "DROP TABLE IF EXISTS rappel;";
    public static final String TC_TRAITEMENT_ZONE_TABLE_DROP = "DROP TABLE IF EXISTS tc_traitement_zone;";
    public static final String ZONE_TABLE_DROP = "DROP TABLE IF EXISTS zone;";
    public static final String RAPPELPREF_TABLE_DROP = "DROP TABLE IF EXISTS delaipref;";
    public static final String PRISE_TABLE_DROP = "DROP TABLE IF EXISTS prise;";
    public static final String TC_STOCK_TRAITEMENT_TABLE_DROP = "DROP TABLE IF EXISTS tc_stock_traitement";
    public static final String TRAITEMENT_TABLE_DROP = "DROP TABLE IF EXISTS traitement;";
    public static final String STOCK_TABLE_DROP = "DROP TABLE IF EXISTS stock;";
    public static final String TYPE_TABLE_DROP = "DROP TABLE IF EXISTS type;";
    public static final String TAUXHORMONAL_TABLE_DROP = "DROP TABLE IF EXISTS tauxhormonaux;";
    public static final String ALARME_TABLE_DROP = "DROP TABLE IF EXISTS alarme;";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Créer tables de la Base

        Log.d("^^^^^^^^^^^^^^^^^^^^^^^", " ^^^^^^^ :: Création base appelée");

        db.execSQL(TYPE_TABLE_CREATE);
        db.execSQL(STOCK_TABLE_CREATE);
        db.execSQL(TRAITEMENT_TABLE_CREATE);
        db.execSQL(TC_STOCK_TRAITEMENT_TABLE_CREATE);
        db.execSQL(PRISE_TABLE_CREATE);
        db.execSQL(RAPPELPREF_TABLE_CREATE);
        db.execSQL(ZONE_TABLE_CREATE);
        db.execSQL(TC_TRAITEMENT_ZONE_TABLE_CREATE);
        db.execSQL(RAPPEL_TABLE_CREATE);
        db.execSQL(ORDONNANCE_TABLE_CREATE);
        db.execSQL(TC_ORDO_TRAITEMENT_CREATE);
        db.execSQL(TAUXHORMONAL_TABLE_CREATE);
        db.execSQL(ALARME_TABLE_CREATE);


        // remplir table Type avec chaque type de l'enum

        for(Type t : Type.values()) {
            String requete = "INSERT INTO type VALUES (null, '"+t.name().toString()+"',  '"+t.getDenom().toString()+"', '"+t.getAccord()+"')";
            db.execSQL(requete);
        }

        //zones prédéfinies

        db.execSQL("INSERT INTO zone VALUES(null, 'cuisse', 'droite')");
        db.execSQL("INSERT INTO zone VALUES(null, 'cuisse', 'gauche')");
        db.execSQL("INSERT INTO zone VALUES(null, 'fesse', 'droite')");
        db.execSQL("INSERT INTO zone VALUES(null, 'fesse', 'gauche')");
        db.execSQL("INSERT INTO zone VALUES(null, 'jambe', 'droite')");
        db.execSQL("INSERT INTO zone VALUES(null, 'jambe', 'gauche')");
        db.execSQL("INSERT INTO zone VALUES(null, 'bras', 'droit')");
        db.execSQL("INSERT INTO zone VALUES(null, 'bras', 'gauche')");
        db.execSQL("INSERT INTO zone VALUES(null, 'abdomen', 'gauche')");
        db.execSQL("INSERT INTO zone VALUES(null, 'abdomen', 'droit')");
        db.execSQL("INSERT INTO zone VALUES(null, 'abdomen', '(aucun)')");
        db.execSQL("INSERT INTO zone VALUES(null, 'épaule', 'droite')");
        db.execSQL("INSERT INTO zone VALUES(null, 'épaule', 'gauche')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(TC_ORDO_TRAITEMENT_TABLE_DROP);
        db.execSQL(ORDONNANCE_TABLE_DROP);
        db.execSQL(RAPPEL_TABLE_DROP);
        db.execSQL(TC_TRAITEMENT_ZONE_TABLE_DROP);
        db.execSQL(ZONE_TABLE_DROP);
        db.execSQL(PRISE_TABLE_DROP);
        db.execSQL(RAPPELPREF_TABLE_DROP);
        db.execSQL(TC_STOCK_TRAITEMENT_TABLE_DROP);
        db.execSQL(TRAITEMENT_TABLE_DROP);
        db.execSQL(STOCK_TABLE_DROP);
        db.execSQL(TYPE_TABLE_DROP);
        db.execSQL(TAUXHORMONAL_TABLE_DROP);
        db.execSQL(ALARME_TABLE_DROP);

        onCreate(db);
    }




}
