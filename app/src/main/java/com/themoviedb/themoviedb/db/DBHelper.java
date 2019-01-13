package com.themoviedb.themoviedb.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper dbHelper;
    private static final String DB_NAME = "themovie.db";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE_FAVORITE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + DBReaderContract.DBFavoriteEntry.TABLE_NAME + " (" +
            DBReaderContract.DBFavoriteEntry._ID + " INTEGER PRIMARY KEY," +
            DBReaderContract.DBFavoriteEntry.COLUMN_NAME_ID+ " INTEGER)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDataBase(db,0,DB_VERSION);
    }
    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void updateDataBase(SQLiteDatabase db,int oldVersion,int newVersion){
        if(oldVersion<1){
            db.execSQL(DB_CREATE_FAVORITE_ENTRIES);
        }
    }
    public static DBHelper getInstance(){
            return dbHelper;

    }
    public static void createInstance(Context context){
        dbHelper = new DBHelper(context);
    }
}
