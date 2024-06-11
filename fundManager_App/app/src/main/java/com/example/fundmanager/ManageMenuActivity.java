package com.example.fundmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ManageMenuActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_managemenu);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
    }
    public void LogOutManager(View target){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void showFund(View target){
        Intent intent = new Intent(getApplicationContext(), ShowFundActivity.class);
        startActivity(intent);
    }
    public void showUsers(View target){
        Intent intent = new Intent(getApplicationContext(), ShowUserActivity.class);
        startActivity(intent);
    }
}