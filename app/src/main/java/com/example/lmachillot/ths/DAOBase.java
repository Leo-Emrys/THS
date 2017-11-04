package com.example.lmachillot.ths;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

/**
 * Created by lmachillot on 13/03/17.
 */

public abstract class DAOBase {

    protected final static int VERSION = 1;
    protected final static String NOM = "ths.db";



    protected SQLiteDatabase mDb = null;

    protected DatabaseHandler mHandler = null;



    public DAOBase(Context pContext) {

        this.mHandler = new DatabaseHandler(pContext);

    }

    public DatabaseHandler getmHandler() {
        return this.mHandler;
    }



    public SQLiteDatabase open() {

        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge

        mDb = mHandler.getWritableDatabase();

        return mDb;

    }



    public void close() {

        mDb.close();

    }



    public SQLiteDatabase getDb() {

        return mDb;

    }

}