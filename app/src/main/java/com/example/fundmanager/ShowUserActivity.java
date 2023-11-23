package com.example.fundmanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShowUserActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText findName;
    TextView result;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_manage);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
        findName = (EditText) findViewById(R.id.searchName);
        result = (TextView) findViewById(R.id.memberInfoText);
    }

    public void search(View target) {
        String name = findName.getText().toString();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user WHERE name='"
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
        result.setText(s);
        cursor.close();
    }
    public void select_all(View target) {
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM user", null);

        String s="Id            Pw            Name            Account \r\n";
        while (cursor.moveToNext()) {
            s += cursor.getString(0) + "    ";
            s += cursor.getString(1) + "    ";
            s += cursor.getString(2) + "    ";
            s += cursor.getString(3) + "    \r\n";
        }
        result.setText(s);
        cursor.close();
    }
}
