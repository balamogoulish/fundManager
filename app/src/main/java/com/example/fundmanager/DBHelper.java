package com.example.fundmanager;

import android.database.DatabaseUtils;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FundManager.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE user ( userid INTEGER PRIMARY KEY, password TEXT, name TEXT, account TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public boolean insertUser(int id, String password, String name, String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO user VALUES ('" + id + "', '" + password + "', '" + name + "', '" + account + "');");
        return true;
    }

    public Cursor select_one(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM user WHERE userid='"
                + id + "';", null);
        return res;
    }

    public Cursor select_all() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from user", null);
        return res;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM user WHERE userid='"
                + id + "';", null);
        return true;
    }

}
