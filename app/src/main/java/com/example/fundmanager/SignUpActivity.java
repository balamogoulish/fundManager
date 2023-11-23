package com.example.fundmanager;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Bundle;
import android.util.Log;
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
        try {
            db.execSQL("INSERT INTO user VALUES ('" + id + "', '" + pw + "', '" + name + "', '" + account + "');");
            Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
            edit_id.setText("");
            edit_pw.setText("");
            edit_name.setText("");
            edit_account.setText("");
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Failed to insert data. Please try again", Toast.LENGTH_SHORT).show();
            // You can log the exception for further analysis if needed
            Log.e("Insert Error", "Error inserting data into the database", e);
        }
    }
}