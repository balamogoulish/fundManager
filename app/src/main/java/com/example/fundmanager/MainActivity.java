package com.example.fundmanager;

import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    DBHelper mydb;
    EditText edit_id, edit_pw, edit_name, edit_account;
    TextView edit_result;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mydb = new DBHelper(this);
        edit_id = (EditText) findViewById(R.id.register_id);
        edit_pw = (EditText) findViewById(R.id.register_pw);
        edit_name = (EditText) findViewById(R.id.register_name);
        edit_account = (EditText) findViewById(R.id.register_account);
        edit_result = (TextView) findViewById(R.id.textView);
    }

    public void insert(View view) {
        int id = Integer.parseInt(edit_id.getText().toString());
        String pw = edit_pw.getText().toString();
        String name = edit_name.getText().toString();
        String account = edit_account.getText().toString();
        if(mydb.insertUser(id, pw, name, account)){ //회원가입 성공 시
            Toast.makeText(getApplicationContext(), "추가되었음", Toast.LENGTH_SHORT).show();
            //회원가입 성공!! -> 로그인 화면으로 이동
        } else {
            Toast.makeText(getApplicationContext(), "추가되지 않았음", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view) {
        int id = Integer.parseInt(edit_id.getText().toString());
        if(mydb.select_one(id) != null){ //조회 성공 시
            Cursor cursor = mydb.select_one(id);
            String s="Id            Pw            Name            Account \r\n";
            while (cursor.moveToNext()) {
                s += cursor.getString(0) + "    ";
                s += cursor.getString(1) + "    ";
                s += cursor.getString(2) + "    ";
                s += cursor.getString(3) + "    \r\n";
            }
            edit_result.setText(s);
        } else {
            edit_result.setText("조회 데이터가 존재하지 않습니다.");
        }
    }
    public void search_all(View target) {
        if(mydb.select_all() != null){ //조회 성공 시
            Cursor cursor = mydb.select_all();
            String s="Id            Pw            Name            Account \r\n";
            while (cursor.moveToNext()) {
                s += cursor.getString(0) + "    ";
                s += cursor.getString(1) + "    ";
                s += cursor.getString(2) + "    ";
                s += cursor.getString(3) + "    \r\n";
            }
            edit_result.setText(s);
        } else {
            edit_result.setText("조회 데이터가 존재하지 않습니다.");
        }
    }
}