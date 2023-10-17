package com.minorproject.bidnow;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class WonAuctionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Auction> auctionsList;
    private VictoryAuctionAdapter adapter;
    private TextView oopsMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_victory_auctions);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        recyclerView = findViewById(R.id.recyclerVictoryActivityView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(WonAuctionsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        oopsMessage = findViewById(R.id.oopsMessage);

        // Initialize the ArrayList
        auctionsList = new ArrayList<>();

        DatabaseReference auctionsRef = FirebaseDatabase.getInstance().getReference("Auctions");

//        auctionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot auctionSnapshot : snapshot.getChildren()) {
//                    Auction auction = auctionSnapshot.getValue(Auction.class);
//                    if (auction != null && Objects.requireNonNull(auctionSnapshot.child("auctionId").getValue(String.class)).startsWith(userId)) {
//                        auctionsList.add(auction);
//                    }
//                }
//
//                // Create and set the adapter after fetching the data
//                adapter = new ActivityAuctionAdapter(getApplicationContext(),auctionsList);
//                recyclerView.setAdapter(adapter);
//
//                // Notify the adapter that the data has changed
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle any errors
//            }
//        });

            auctionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    auctionsList.clear();
                    for(DataSnapshot auctionSnapshot : snapshot.getChildren()){
                        Auction auction = auctionSnapshot.getValue(Auction.class);
                        assert auction != null;
                        if(auction.getWinnerId() != null && auction.getWinnerId().equals(userId)){
                            auctionsList.add(auction);
                        }
                    }
                    if(auctionsList.isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        oopsMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        oopsMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new VictoryAuctionAdapter(WonAuctionsActivity.this ,auctionsList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
}
