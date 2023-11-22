package com.example.fundmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mycontacts.db";

    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts ( _id INTEGER PRIMARY KEY, password TEXT, name TEXT, account TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
}

public class MainActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText edit_id, edit_pw, edit_name, edit_account;
    TextView edit_result;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
        edit_id = (EditText) findViewById(R.id.register_id);
        edit_pw = (EditText) findViewById(R.id.register_pw);
        edit_name = (EditText) findViewById(R.id.register_name);
        edit_account = (EditText) findViewById(R.id.register_account);
        edit_result = (TextView) findViewById(R.id.textView);
    }

    public void insert(View target) {
        String id = edit_id.getText().toString();
        String pw = edit_pw.getText().toString();
        String name = edit_name.getText().toString();
        String account = edit_account.getText().toString();
        db.execSQL("INSERT INTO contacts VALUES ('" + id + "', '" + pw + "', '" + name + "', '" + account + "');");
        Toast.makeText(getApplicationContext(), "성공적으로 추가되었음",
                Toast.LENGTH_SHORT).show();
        edit_id.setText("");
        edit_pw.setText("");
        edit_name.setText("");
        edit_account.setText("");
    }

    public void search(View target) {
        String name = edit_name.getText().toString();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM contacts WHERE name='"
                + name + "';", null);

        Toast.makeText(getApplicationContext(), ""+cursor.getCount() ,
                Toast.LENGTH_SHORT).show();

        String s="Id            Pw            Name            Account \r\n";
        while (cursor.moveToNext()) {
            s += cursor.getString(0) + "    ";
            s += cursor.getString(1) + "    ";
            s += cursor.getString(2) + "    ";
            s += cursor.getString(3) + "    \r\n";
        }
        edit_result.setText(s);
    }
    public void select_all(View target) {
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM contacts", null);

        String s="Id            Pw            Name            Account \r\n";
        while (cursor.moveToNext()) {
            s += cursor.getString(0) + "    ";
            s += cursor.getString(1) + "    ";
            s += cursor.getString(2) + "    ";
            s += cursor.getString(3) + "    \r\n";
        }
        edit_result.setText(s);

    }
}