package com.yuahn19plus.qrmember;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private FirebaseFirestore db; // 클라우드 파이어스토어
    private EditText mEtEmail, mEtPwd, mEtPwdCheck, mEtName, mEtPhone, mEtBirthday, mEtAddr;
    private Button mBtnRegister, mBtnIdCheck;

    private boolean idChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증
        db =FirebaseFirestore.getInstance();        // 파이어베이스 파이어스토어

        // 에디트 텍스트
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mEtPwdCheck = (EditText) findViewById(R.id.et_pwd_check);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtBirthday = (EditText) findViewById(R.id.et_birthday);
        mEtAddr = (EditText) findViewById(R.id.et_addr);

        // 버튼
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnIdCheck = (Button) findViewById(R.id.btn_id_check);

        // 아이디를 재입력시 중복확인을 추가로 할 수 있도록 처리
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                idChecked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 아이디 중복확인
        mBtnIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = mEtEmail.getText().toString();
                if(strEmail.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    mFirebaseAuth.fetchSignInMethodsForEmail(strEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if(isNewUser){
                                    Toast.makeText(RegisterActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                                    idChecked = true;
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "이미 사용중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "이메일 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                

            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdCheck = mEtPwdCheck.getText().toString();
                // 사용자가 입력한 회원가입 정보 추출
                FirebaseUser firebaseUser;
                String strName = mEtName.getText().toString();
                String strPhone = mEtPhone.getText().toString();
                String strAddr = mEtAddr.getText().toString();
                String strBirthday = mEtBirthday.getText().toString();
                if(strEmail.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(idChecked){
                        if(strPwd.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if(strPwdCheck.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if(!strPwd.equals(strPwdCheck)){
                            Toast.makeText(RegisterActivity.this, "비밀번호 확인에 입력하신 비밀번호가 입력한 비밀번호와 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else if(strName.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if(strPhone.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if(strAddr.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if(strBirthday.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //FirebaseAuth 진행
                            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // Firebase에 저장할 데이터 객체 생성
                                    if(task.isSuccessful()){
                                        // 사용자 생성 성공, 사용자의 UID 가져오기
                                        FirebaseUser firebaseUser = task.getResult().getUser();
                                        String uid = firebaseUser.getUid();  // 이 UID를 문서 ID로 사용

                                        // Firebase에 저장할 데이터 객체 생성
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("uid", uid);
                                        user.put("name", strName);
                                        user.put("phone", strPhone);
                                        user.put("birthday", strBirthday);
                                        user.put("addr", strAddr);
                                        user.put("joindate", new Date());
                                        user.put("point", 50);

                                        // "User" 컬렉션에 데이터 추가
                                        db.collection("User").document(uid).set(user).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, "회원가입 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                        }

                    }

                    else{
                        Toast.makeText(RegisterActivity.this, "아이디 중복체크를 진행해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}