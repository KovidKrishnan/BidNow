package com.minorproject.bidnow;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyActivityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity);

        recyclerView = findViewById(R.id.activityRecyclerView);

        DatabaseReference createdAuctionsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CreatedAuctions");

        DatabaseReference auctionsRef = FirebaseDatabase.getInstance().getReference("Auctions");


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyActivityActivity.this, HomeActivity.class));
    }

}
