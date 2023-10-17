package com.minorproject.bidnow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyActivityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VictoryAuctionAdapter adapter;
    private ArrayList<Auction> auctionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_screen);

        CardView created = findViewById(R.id.my_created_auctions);
        CardView victory = findViewById(R.id.my_won_auctions);

        created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyActivityActivity.this, CreatedAuctionsActivity.class));
            }
        });

        victory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyActivityActivity.this, WonAuctionsActivity.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyActivityActivity.this, HomeActivity.class));
    }
}
