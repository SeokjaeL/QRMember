package com.yuahn19plus.qrmember;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText mEtAdminId;
    private EditText mEtAdminPwd;
    private Button mBtnAdminLogin;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mEtAdminId = findViewById(R.id.et_adminid);
        mEtAdminPwd = findViewById(R.id.et_adminpwd);
        mBtnAdminLogin = findViewById(R.id.btn_adminlogin);
        db = FirebaseFirestore.getInstance();
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);

        mBtnAdminLogin.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        String adminId = mEtAdminId.getText().toString().trim();
        String adminPwd = mEtAdminPwd.getText().toString().trim();

        if (adminId.isEmpty() || adminPwd.isEmpty()) {
            Toast.makeText(this, "관리자 아이디나 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(adminPwd);
        db.collection("Admin").document(adminId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists() && task.getResult().getString("password").equals(hashedPassword)) {
                    Toast.makeText(AdminLoginActivity.this, "관리자 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    // Save the admin ID in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("admin_id", adminId);
                    editor.apply();

                    // Navigate to AdminMenuActivity
                    Intent intent = new Intent(AdminLoginActivity.this, AdminMenuActivity.class);
                    startActivity(intent);
                    finish(); // Finish this activity so the user cannot return to it
                } else {
                    Toast.makeText(AdminLoginActivity.this, "관리자 아이디나 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminLoginActivity.this, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}