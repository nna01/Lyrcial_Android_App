package com.lyrical.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class SavedSongsDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "songlist.db";
    public static final String TABLE_NAME = "song_table";
    public static final String COLUMN_1 = "NAME";
    public static final String COLUMN_2 = "PATH";

    public SavedSongsDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME +" (NAME TEXT, PATH TEXT)");
        //db.execSQL("create table " + TABLE_NAME +" (NAME TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME );
        onCreate(db);
    }

    public boolean insertData(String name , String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(COLUMN_1 , name);
        contentValues.put(COLUMN_2 , path);
        long ans = db.insert(TABLE_NAME , null , contentValues);

        if(ans == -1)
            return false;
        return true;
    }

    public Cursor getAllData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM song_table" ,null);
        return res ;
    }
}
