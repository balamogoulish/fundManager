package com.example.fundmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        setContentView(R.layout.activity_menu);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
//        edit_id = (EditText) findViewById(R.id.idEdit);
    }
    public void LogOut(View target){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
//    public void ViewMyGain(View target){
//        Intent intent = new Intent(getApplicationContext(), GainActivity.class);
//        startActivity(intent);
//    }
    public void InOutMoney(View target){
        Intent intent = new Intent(getApplicationContext(), InOutActivity.class);
        startActivity(intent);
    }
    public void UpdateMyInfo(View target){
        Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
        startActivity(intent);
    }


}
