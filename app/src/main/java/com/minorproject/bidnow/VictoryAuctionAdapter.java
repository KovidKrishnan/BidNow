package com.minorproject.bidnow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VictoryAuctionAdapter extends RecyclerView.Adapter<VictoryAuctionAdapter.ViewHolder> {
    private ArrayList<Auction> auctionList;
    private Context context;

    public VictoryAuctionAdapter(Context context, ArrayList<Auction> auctionList) {
        this.context = context;
        this.auctionList = auctionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_auction_live, parent, false);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_auction_live,parent,false);
        VictoryAuctionAdapter.ViewHolder viewHolder = new VictoryAuctionAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VictoryAuctionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Auction model = auctionList.get(position);

        // settexts to be filled

        // Bind the data to the views in the ViewHolder
        DatabaseReference auctionRef = FirebaseDatabase.getInstance().getReference().child("Auctions").child(model.getAuctionId());
        if (model.getAuctionStatus().equals("Completed")) {
            holder.statusTextView.setTextColor(Color.parseColor("#FF2200"));
            holder.declareWinner.setEnabled(false);
            if (String.valueOf(model.getStartingBid()).equals(String.valueOf(model.getCurrentBid()))) {
                holder.contactWinner.setEnabled(false);
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + model.getAuctionStatus());
        }
        holder.declareWinner.setEnabled(false);
        holder.deleteAuction.setEnabled(false);
        holder.titleTextView.setText(model.getTitle());
        holder.statusTextView.setText(model.getAuctionStatus());
        holder.currentBidTextView.setText("Current Bid: ₹" + model.getStartingBid());

        holder.currentBidderTextView.setText("Current Bidder: " + model.getSellerId());
        loadImageUsingGlide(holder.imageView, model.getImageUrl());

        holder.contactWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Users").child("currentBidder").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String mobile = snapshot.child("phone").getValue(String.class);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91" + mobile));
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle any errors
                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return auctionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, statusTextView, currentBidTextView, currentBidderTextView;
        Button deleteAuction, declareWinner, contactWinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.auctionActivityImageView);
            this.titleTextView = itemView.findViewById(R.id.titleActivityTextView);
            this.statusTextView = itemView.findViewById(R.id.auctionStatusActivityTextView);
            this.currentBidTextView = itemView.findViewById(R.id.currentAmountActivityTextView);
            this.currentBidderTextView = itemView.findViewById(R.id.currentBidderActivityTextView);

            this.deleteAuction = itemView.findViewById(R.id.deleteAuctionButton);
            this.declareWinner = itemView.findViewById(R.id.declareWinnerButton);
            this.contactWinner = itemView.findViewById(R.id.contactButton);
        }
    }

    // Replace this method with your image loading library (e.g., Glide or Picasso)
    private void loadImageUsingGlide(ImageView imageView, String imageUrl) {
        // Use Glide to load the image from the URL into the ImageView
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image
                .error(R.drawable.error_image) // Error image if loading fails
                .transition(DrawableTransitionOptions.withCrossFade(1000)) // Add cross-fade transition
                .into(imageView);
    }
}

