package com.yuahn19plus.qrmember;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AdminAddActivity extends AppCompatActivity {
    private EditText mEtAdminId;
    private EditText mEtAdminPwd;
    private Button mBtnAddAdmin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        mEtAdminId = findViewById(R.id.et_adminId);
        mEtAdminPwd = findViewById(R.id.et_adminpwd);
        mBtnAddAdmin = findViewById(R.id.btn_addAdmin);
        db = FirebaseFirestore.getInstance();

        mBtnAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminId = mEtAdminId.getText().toString().trim();
                String adminPwd = mEtAdminPwd.getText().toString().trim();

                if (adminId.isEmpty()) {
                    Toast.makeText(AdminAddActivity.this, "관리자 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (adminPwd.isEmpty()) {
                    Toast.makeText(AdminAddActivity.this, "관리자 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                String hashedPassword = hashPassword(adminPwd);

                db.collection("Admin").document(adminId).set(Collections.singletonMap("password", hashedPassword))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminAddActivity.this, "관리자 추가에 성공했습니다.", Toast.LENGTH_SHORT).show();
                            // Navigate to AdminMenuActivity
                            finish(); // Optionally finish this activity
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminAddActivity.this, "관리자 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}
