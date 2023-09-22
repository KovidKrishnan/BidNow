package com.minorproject.bidnow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuctionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuctionAdapter adapter;
    private String category;
    private ProgressBar loader;

    public AuctionsFragment(String category){
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auction_list, container, false);


        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Fetch and display "Live" auctions using FirebaseRecyclerOptions
        DatabaseReference auctionsRef = FirebaseDatabase.getInstance().getReference().child("Auctions");
        FirebaseRecyclerOptions<Auction> options =
                new FirebaseRecyclerOptions.Builder<Auction>()
                        .setQuery(auctionsRef.orderByChild("auctionStatus").equalTo(category), Auction.class)
                        .build();

        adapter = new AuctionAdapter(options);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}