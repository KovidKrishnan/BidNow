package com.minorproject.bidnow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RazorPayPaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private EditText payAmount;
    private int amount;
    private DatabaseReference userRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay_payment);

        Button payBtn = findViewById(R.id.btnPay);
        payAmount = findViewById(R.id.etAmount);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountString = payAmount.getText().toString();
                amount = Math.round(Float.parseFloat(amountString));

                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_sP0GiiCWIECpcC");

                JSONObject object = new JSONObject();

                try{
                    object.put("name","BidNow");
                    object.put("currency", "INR");
                    object.put("amount",amount);

                    checkout.open(RazorPayPaymentActivity.this, object);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(RazorPayPaymentActivity.this, "Wallet Credited", Toast.LENGTH_SHORT).show();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //noinspection DataFlowIssue
                double amountRem = snapshot.child("creditValue").getValue(Double.class);
                userRef.child("creditValue").setValue(amountRem + amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RazorPayPaymentActivity.this, "Wallet Credited", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RazorPayPaymentActivity.this, HomeActivity.class));
                        }
                        else{
                            Toast.makeText(RazorPayPaymentActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RazorPayPaymentActivity.this, HomeActivity.class));
                        }
                    }
                });
                startActivity(new Intent(RazorPayPaymentActivity.this, HomeActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(RazorPayPaymentActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RazorPayPaymentActivity.this, HomeActivity.class));
    }
}