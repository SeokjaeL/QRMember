package com.yuahn19plus.qrmember;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PayActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        payload.setApplicationId("660c0ed6d7005fd6c24ec046")
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
                    // 결제 완료
                    public void onDone(String data) {
                        Toast.makeText(PayActivity.this, "결제 성공: " + data, Toast.LENGTH_LONG).show();
                        
                        // Firestore에 저장할 데이터 생성
                        Map<String, Object> paymentInfo = new HashMap<>();
                        paymentInfo.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        paymentInfo.put("amount", amount);
                        paymentInfo.put("order_id", "1234");
                        paymentInfo.put("receipt", data); // 이 부분은 반환된 결제 영수증 정보나 필요한 데이터를 포함합니다.
                        paymentInfo.put("timestamp", FieldValue.serverTimestamp()); // 서버 시간을 사용하여 결제 시각 기록

                        // Firestore에 데이터 저장
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("payments").add(paymentInfo)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error adding document", e);
                                    Toast.makeText(PayActivity.this, "Firestore 저장 실패", Toast.LENGTH_SHORT).show();
                                });


                        finish();
                    }
                }).requestPayment();
    }
}
