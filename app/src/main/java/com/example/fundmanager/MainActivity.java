package com.example.fundmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FUNDMANAGER.DB";

    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user ( _id INTEGER PRIMARY KEY" +" AUTOINCREMENT, userId TEXT, password TEXT, name TEXT, account TEXT);");
        db.execSQL("CREATE TABLE investment ( investId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_id INTEGER, " +
                "fundId INTEGER, " +
                "investMoney INTEGER, " +
                "date TEXT, " +
                "FOREIGN KEY(_id) REFERENCES user(_id)," +
                "FOREIGN KEY(fundId) REFERENCES fund(fundId));");
        db.execSQL("CREATE TABLE inquirement ( _id INTEGER PRIMARY KEY, " +
                "fundId INTEGER, " +
                "money INTEGER, " +
                "perGain INTEGER, " +
                "FOREIGN KEY(_id) REFERENCES user(_id)," +
                "FOREIGN KEY(fundId) REFERENCES fund(fundId));");

        db.execSQL("CREATE TABLE fund ( fundId INTEGER PRIMARY KEY, " +
                "inputFund INTEGER, " +
                "outputFund INTEGER, " +
                "totalGain INTEGER, " +
                "date TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }
}

public class MainActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText edit_id, edit_pw;
    Button signupBtn;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }
        edit_id = (EditText) findViewById(R.id.idEdit);
        edit_pw = (EditText) findViewById(R.id.pwEdit);
        signupBtn = (Button) findViewById(R.id.signupBtn);
    }

    public void search(View target) {
        String id = edit_id.getText().toString();
        String pw = edit_pw.getText().toString();
        Cursor res = db.rawQuery("SELECT name, _id FROM user WHERE userId='" + id + "' AND password='" + pw + "';", null);
        if (res!=null && res.moveToFirst()) { // Cursor에서 데이터가 있을 경우
            Toast.makeText(getApplicationContext(), "로그인에 성공했습니다! ", Toast.LENGTH_SHORT).show();
            // MenuActivity로 이동

            if("ADMIN".equals(res.getString(0))){
                Intent intentAdmin = new Intent(getApplicationContext(), ManageMenuActivity.class);
                startActivity(intentAdmin);
                finish();
            } else {
                Intent intentUser = new Intent(getApplicationContext(), MenuActivity.class);
                intentUser.putExtra("_id", res.getString(1));
                startActivity(intentUser);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다,\n 다시 시도해주세요", Toast.LENGTH_SHORT).show();
        }
        res.close();
    }

    public void goToSignUp(View target){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}