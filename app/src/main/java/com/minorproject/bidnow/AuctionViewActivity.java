package com.minorproject.bidnow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AuctionViewActivity extends AppCompatActivity {

    private TextView auctionTitle, auctionDescription, auctionStartBid, auctionCurrentBid, auctionStatus, countDownTextView;
    private ImageView auctionImageView;
    private CountDownTimer auctionCountdownTimer;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference auctionRef;
    private Long endtime, starttime;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_view);

        auctionTitle = findViewById(R.id.auctionTitleTextView);
        auctionDescription = findViewById(R.id.productDescriptionTextView);
        auctionStartBid = findViewById(R.id.startingBidTextView);
        auctionCurrentBid = findViewById(R.id.currentBidTextViewAuctionPage);
        auctionStatus = findViewById(R.id.auctionStatusTextView);
        countDownTextView = findViewById(R.id.countDownTextView);
        auctionImageView = findViewById(R.id.auctionImageView);

        animateElementsUp();
        firebaseDatabase = FirebaseDatabase.getInstance();
        auctionRef = firebaseDatabase.getReference("Auctions").child(Objects.requireNonNull(getIntent().getStringExtra("auctionId")));

        auctionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                if (title != null) {
                    auctionTitle.setText(title);
                }

                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                if (imageUrl != null) {
                    Glide.with(auctionImageView.getContext())
                            .load(imageUrl)
                            .into(auctionImageView);
                }

                String description = snapshot.child("description").getValue(String.class);
                if (description != null) {
                    auctionDescription.setText(description);
                }

//                String auctionStatusStr = snapshot.child("auctionStatus").getValue(String.class);
//                if (auctionStatusStr != null) {
//                    auctionStatus.setText(auctionStatusStr);

                    switch (Objects.requireNonNull(snapshot.child("auctionStatus").getValue(String.class))) {
                        case "Upcoming":
                            auctionStatus.setTextColor(Color.parseColor("#FFA500"));
                            break;
                        case "Live":
                            auctionStatus.setTextColor(Color.parseColor("#00FF22"));
                            break;
                        case "Completed":
                            auctionStatus.setTextColor(Color.parseColor("#FF2200"));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: ");
                    }

                Long currentBidLong = snapshot.child("currentBid").getValue(Long.class);
                if (currentBidLong != null) {
                    auctionCurrentBid.setText("₹" + currentBidLong.toString());
                }

                Long startingBidLong = snapshot.child("startingBid").getValue(Long.class);
                if (startingBidLong != null) {
                    auctionStartBid.setText("₹" + startingBidLong.toString());
                }

                starttime = snapshot.child("startDate").getValue(Long.class);
                endtime = snapshot.child("endDate").getValue(Long.class);
                startCountdown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Error: " + error.getMessage());
                // You can also show an error message to the user
            }
        });
    }

    private void startCountdown() {
        if (endtime != null && starttime != null) {
            // Calculate the remaining time in milliseconds
            boolean live = false;
            Long currentTimeMillis = System.currentTimeMillis();
            Long timeLeftMillis = starttime - currentTimeMillis;
            if (timeLeftMillis <= 0) {
                timeLeftMillis = endtime - currentTimeMillis;
                if(timeLeftMillis <= 0){
                    auctionStatus.setText("Completed");
                }
                else{
                    auctionRef.child("auctionStatus").setValue("Live");
                    auctionStatus.setText("Live");
                    live = true;
                }

            }
            else{
                auctionStatus.setText("Upcoming");
            }
            if (countDownTextView != null) {
                if (timeLeftMillis > 0) { // Ensure the auction is not already ended
                    // Create and start the countdown timer
                    boolean finalLive = live;
                    auctionCountdownTimer = new CountDownTimer(timeLeftMillis, 1000) {
                        @SuppressLint("SetTextI18n")
                        public void onTick(long millisUntilFinished) {
                            // Calculate days, hours, minutes, and seconds from millisUntilFinished
                            long days = millisUntilFinished / (24 * 60 * 60 * 1000);
                            millisUntilFinished -= days * (24 * 60 * 60 * 1000);
                            long hours = millisUntilFinished / (60 * 60 * 1000);
                            millisUntilFinished -= hours * (60 * 60 * 1000);
                            long minutes = millisUntilFinished / (60 * 1000);
                            millisUntilFinished -= minutes * (60 * 1000);
                            long seconds = millisUntilFinished / 1000;

                            // Update the countdown TextView with the remaining time
                            @SuppressLint("DefaultLocale") String countdownText = String.format("%02d days :%02d:%02d:%02d", days, hours, minutes, seconds);
                            if (finalLive)
                                countDownTextView.setText("Ends in: " + countdownText);
                            else
                                countDownTextView.setText("Starts in: " + countdownText);
                        }

                        @SuppressLint("SetTextI18n")
                        public void onFinish() {
                            // Auction has ended, handle this as needed
                            countDownTextView.setText("Auction Ended");
                            auctionStatus.setText("Completed");
                            auctionRef.child("auctionStatus").setValue("Completed");
                        }
                    }.start();
                } else {
                    countDownTextView.setText("Auction Ended"); // Handle case where the auction has already ended
                }
            }
        }
    }
    private void animateElementsUp() {
        // Get references to your elements that you want to animate
        TextView auctionDescription = findViewById(R.id.productDescriptionTextView);
        TextView auctionStartBid = findViewById(R.id.startingBidTextView);
        TextView auctionStatus = findViewById(R.id.auctionStatusTextView);
        TextView auctionTitle = findViewById(R.id.auctionTitleTextView);
        TextView countDownTextView = findViewById(R.id.countDownTextView);
        CardView auctionImageView = findViewById(R.id.pathRelative);
        LinearLayout buttonPanel = findViewById(R.id.buttonPanel);

        // Create animations for each element
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation5 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation6 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);
        Animation animation7 = AnimationUtils.loadAnimation(this, R.anim.slide_up_animation);

        // Set the delay for each animation to create a staggered effect
        animation1.setStartOffset(0);      // No delay for the first element
        animation2.setStartOffset(200);    // Delay for the second element (in milliseconds)
        animation3.setStartOffset(400);    // Delay for the third element
        animation4.setStartOffset(600);    // Delay for the fourth element
        animation5.setStartOffset(800);    // Delay for the fifth element
        animation6.setStartOffset(1000);   // Delay for the sixth element
        animation7.setStartOffset(1200);   // Delay for the seventh element

        // Start animations for each element
        auctionTitle.startAnimation(animation1);
        auctionImageView.startAnimation(animation2);
        countDownTextView.startAnimation(animation3);
        auctionStatus.startAnimation(animation3);
        auctionStartBid.startAnimation(animation4);
        auctionDescription.startAnimation(animation5);



        buttonPanel.startAnimation(animation6);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (auctionCountdownTimer != null) {
            auctionCountdownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AuctionViewActivity.this, AuctionListingActivity.class));
    }

}