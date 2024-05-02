package com.yuahn19plus.qrmember;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminMenuActivity extends AppCompatActivity {
    private Button mBtnAddAdmin;
    private Button mBtnAdminManagement;
    private Button mBtnAdminInfo;
    private Button mBtnAdminLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        mBtnAddAdmin = (Button) findViewById(R.id.btn_addAdmin);
        mBtnAdminManagement = (Button) findViewById(R.id.btn_adminManagement);
        mBtnAdminInfo = (Button) findViewById(R.id.btn_adminInfo);
        mBtnAdminLogout = (Button) findViewById(R.id.btn_adminlogout);

        mBtnAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminAddActivity.class);
                startActivity(intent);
            }
        });

        mBtnAdminInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferences에서 관리자 아이디 가져오기
                SharedPreferences sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", "Unknown"); // 기본값으로 "Unknown" 설정

                // 관리자 아이디를 토스트 메시지로 표시
                Toast.makeText(getApplicationContext(), "현재 로그인한 관리자 아이디는 " + adminId + "입니다.", Toast.LENGTH_LONG).show();
            }
        });

        mBtnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session information
                SharedPreferences preferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("admin_id");
                editor.apply();

                Toast.makeText(AdminMenuActivity.this, "로그아웃을 완료했습니다.", Toast.LENGTH_SHORT).show();

                // Navigate back to the main activity
                Intent intent = new Intent(AdminMenuActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}