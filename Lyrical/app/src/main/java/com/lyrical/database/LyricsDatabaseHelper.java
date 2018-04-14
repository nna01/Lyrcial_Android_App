package com.lyrical.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LyricsDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "lyrics.db";
    public static final String TABLE_NAME = "lyrics_table";
    public static final String COLUMN_1 = "LYRICS";
    public static final String COLUMN_2 = "TIME";
    public static final String COLUMN_3 = "ID";

    public LyricsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME +" (LYRICS TEXT, TIME INTEGER, ID INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME );
        onCreate(db);
    }

    public boolean insertData(String lyrics , long time, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(COLUMN_1 , lyrics );
        contentValues.put(COLUMN_2 , time );
        contentValues.put(COLUMN_3 , id );
        long ans = db.insert(TABLE_NAME , null , contentValues);

        if(ans == -1)
            return false;
        return true;
    }

    public Cursor getAllData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM lyrics_table" ,null);
        return res ;
    }
}
