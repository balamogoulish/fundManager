package com.example.fundmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText edit_id, edit_pw, edit_name, edit_account;
    //TextView edit_result;

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
        //sedit_result = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
    }

    public void insert(View target) {
        //To do: check id, pw, name, account valid
        String id = edit_id.getText().toString();
        String pw = edit_pw.getText().toString();
        String name = edit_name.getText().toString();
        String account = edit_account.getText().toString();
        db.execSQL("INSERT INTO user VALUES ('" + id + "', '" + pw + "', '" + name + "', '" + account + "');");
        Toast.makeText(getApplicationContext(), "성공적으로 추가되었음",
                Toast.LENGTH_SHORT).show();
        edit_id.setText("");
        edit_pw.setText("");
        edit_name.setText("");
        edit_account.setText("");
    }

//    public void search(View target) {
//        String name = edit_name.getText().toString();
//        Cursor cursor;
//        cursor = db.rawQuery("SELECT * FROM user WHERE name='"
//                + name + "';", null);
//
//        Toast.makeText(getApplicationContext(), ""+cursor.getCount() ,
//                Toast.LENGTH_SHORT).show();
//
//        String s="Id            Pw            Name            Account \r\n";
//        while (cursor.moveToNext()) {
//            s += cursor.getString(0) + "    ";
//            s += cursor.getString(1) + "    ";
//            s += cursor.getString(2) + "    ";
//            s += cursor.getString(3) + "    \r\n";
//        }
//        edit_result.setText(s);
//    }
//    public void select_all(View target) {
//        Cursor cursor;
//        cursor = db.rawQuery("SELECT * FROM user", null);
//
//        String s="Id            Pw            Name            Account \r\n";
//        while (cursor.moveToNext()) {
//            s += cursor.getString(0) + "    ";
//            s += cursor.getString(1) + "    ";
//            s += cursor.getString(2) + "    ";
//            s += cursor.getString(3) + "    \r\n";
//        }
//        edit_result.setText(s);
//
//    }
}