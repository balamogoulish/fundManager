package com.example.fundmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    String _id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        _id = intent.getStringExtra("_id");

        setContentView(R.layout.activity_menu);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
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
        intent.putExtra("_id", _id);
        startActivity(intent);
    }
    public void UpdateMyInfo(View target){
        Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
        intent.putExtra("_id", _id);
        startActivity(intent);
    }


}
