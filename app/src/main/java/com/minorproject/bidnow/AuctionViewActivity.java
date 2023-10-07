package com.minorproject.bidnow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private Button addBid;
    private Long endtime, starttime;
    private BidAdapter adapter;

    private RecyclerView bidsRecycler;
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
        addBid = findViewById(R.id.placeBidButton);

        animateElementsUp();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String auctionId = getIntent().getStringExtra("auctionId");
        auctionRef = firebaseDatabase.getReference("Auctions").child(auctionId);

        bidsRecycler = findViewById(R.id.bidsListView);
        bidsRecycler.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Auctions").child(auctionId).child("Bids");

        FirebaseRecyclerOptions<Bid> options =
                new FirebaseRecyclerOptions.Builder<Bid>()
                        .setQuery(databaseReference, Bid.class)
                        .build();

        adapter = new BidAdapter(options);
        bidsRecycler.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                scrollToBottom();
            }

            private void scrollToBottom() {
                bidsRecycler.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

// Define a method to scroll to the bottom of the RecyclerView


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

                String auctionStatusStr = snapshot.child("auctionStatus").getValue(String.class);
                if (auctionStatusStr != null) {
                    auctionStatus.setText(auctionStatusStr);
                    switch (auctionStatusStr){
                        case "Live":
                            auctionStatus.setTextColor(Color.parseColor("#008000"));
                            break;
                        case "Upcoming":
                            auctionStatus.setTextColor(Color.parseColor("#FFA500"));
                            break;
                        case "Completed":
                            auctionStatus.setTextColor(Color.parseColor("#FF0000"));
                            addBid.setEnabled(false);
                            break;
                        default:
                            auctionStatus.setTextColor(Color.parseColor("#000000"));
                    }
                }

                Long currentBidLong = snapshot.child("currentBid").getValue(Long.class);
                if (currentBidLong != null) {
                    auctionCurrentBid.setText("₹" + currentBidLong.toString());
                }
                DatabaseReference userref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot usersnapshot) {
                        if(usersnapshot.child("username").getValue(String.class).equals(snapshot.child("sellerId").getValue(String.class))){
                            addBid.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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

        addBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auctionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot auctionSnapshot) {
                        if (auctionSnapshot.child("auctionStatus").getValue(String.class).equals("Live")) {
                            double currentBid = auctionSnapshot.child("currentBid").getValue(Double.class);
                            double amountBid;

                            if (currentBid < 10000) amountBid = 500;
                            else if (currentBid < 25000) amountBid = 1000;
                            else if (currentBid < 100000) amountBid = 2500;
                            else if (currentBid < 500000) amountBid = 5000;
                            else if (currentBid < 1000000) amountBid = 10000;
                            else amountBid = 25000;

                            // Generate a unique key for the bid using push()
                            String bidId = auctionRef.child("Bids").push().getKey();
                            String bidderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            double bidAmount = amountBid + currentBid;
                            long bidTimestamp = System.currentTimeMillis();
                            Bid bid = new Bid(bidId, bidderId, bidAmount, bidTimestamp, auctionId);

                            // Add the bid under a unique key in the "Bids" node
                            auctionRef.child("Bids").child(bidId).setValue(bid)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Update the current bid amount
                                                auctionRef.child("currentBid").setValue(bidAmount)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @SuppressLint("NotifyDataSetChanged")
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Bid and current bid amount updated successfully
                                                                    adapter.notifyDataSetChanged();

                                                                } else {
                                                                    // Failed to update current bid amount
                                                                    Toast.makeText(AuctionViewActivity.this, "Failed to update current bid amount", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Failed to add bid
                                                Toast.makeText(AuctionViewActivity.this, "Unable to add bid!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Handle the case when the auction is not live
                            Toast.makeText(AuctionViewActivity.this, "Auction is not live. Cannot place a bid.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void startCountdown() {
        if (endtime != null && starttime != null) {
            // Calculate the remaining time in milliseconds
            boolean live = false;
            Long currentTimeMillis = System.currentTimeMillis();
            Long timeLeftMillis = starttime - currentTimeMillis;
            if (timeLeftMillis <= 0) {
                timeLeftMillis = endtime - currentTimeMillis;
                if (timeLeftMillis <= 0) {
                    auctionStatus.setText("Completed");
                    auctionRef.child("auctionStatus").setValue("Completed");
                } else {
                    auctionRef.child("auctionStatus").setValue("Live");
                    auctionStatus.setText("Live");
                    live = true;
                }
            } else {
                auctionStatus.setText("Upcoming");
                auctionRef.child("auctionStatus").setValue("Upcoming");
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
                    auctionRef.child("auctionStatus").setValue("Completed");
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
        RecyclerView bidslist = findViewById(R.id.bidsListView);

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
        bidslist.startAnimation(animation7);
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
        // Start the AuctionListingActivity when the back button is pressed
        startActivity(new Intent(AuctionViewActivity.this, AuctionListingActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Start the AuctionListingActivity when the home button is pressed
            startActivity(new Intent(AuctionViewActivity.this, AuctionListingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}