package com.yuahn19plus.qrmember;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button mBtnPaymenu;
    Button mBtnAdmin;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnPaymenu = (Button) findViewById(R.id.btn_paymenu);
        mBtnAdmin = (Button) findViewById(R.id.btn_adminmenu);
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);

        mBtnPaymenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PayActivity.class);
                startActivity(intent);

            }
        });

        mBtnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the admin is already logged in
                String adminId = sharedPreferences.getString("admin_id", null);
                if (adminId != null) {
                    // Admin is logged in, go directly to AdminMenuActivity
                    Intent intent = new Intent(MainActivity.this, AdminMenuActivity.class);
                    startActivity(intent);
                } else {
                    // Admin is not logged in, go to AdminLoginActivity
                    Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}