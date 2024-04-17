package com.yuahn19plus.qrmember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PayActivity extends AppCompatActivity {
    EditText amountEditText;
    Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        amountEditText = findViewById(R.id.amountEditText);
        payButton = findViewById(R.id.payButton);

        FirebaseAuth fa;

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = amountEditText.getText().toString();
                if (!amountString.isEmpty()) {
                    double amount = Double.parseDouble(amountString);
                    initiatePayment(amount);
                } else {
                    Toast.makeText(PayActivity.this, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initiatePayment(double amount) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보
        user.setId(FirebaseAuth.getInstance().getCurrentUser().toString());


        Payload payload = new Payload();
        payload.setApplicationId("부트페이 앱 api 코드")
                .setOrderName("부트페이 결제테스트")
                .setOrderId("1234")
                .setPrice(amount)
                .setUser(user);


        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    //결제 취소
                    public void onCancel(String data) {
                        Toast.makeText(PayActivity.this, "결제 취소: " + data, Toast.LENGTH_LONG).show();
                        finish();

                    }

                    @Override
                    //결제 오류
                    public void onError(String data) {
                        Toast.makeText(PayActivity.this, "결제 오류: " + data, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    // 결제창 닫음
                    public void onClose() {
                        Bootpay.removePaymentWindow();
                        Toast.makeText(PayActivity.this, "결제 창 닫힘", Toast.LENGTH_SHORT).show();
                        finish();
                    }


                    @Override
                    // 이슈 발생(확인 팔요)
                    public void onIssued(String data) {
                        finish();
                    }

                    @Override
                    // 결제 요청
                    public boolean onConfirm(String data) {
                        return true;
                    }

                    @Override
                    // 결제 취소
                    public void onDone(String data) {
                        Toast.makeText(PayActivity.this, "결제 성공: " + data, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).requestPayment();
    }
}
