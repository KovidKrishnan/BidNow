package com.minorproject.bidnow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuctionAdapter extends FirebaseRecyclerAdapter<Auction, AuctionAdapter.ViewHolder> {

    public AuctionAdapter(@NonNull FirebaseRecyclerOptions<Auction> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Auction model) {
        // Bind the data to the views in the ViewHolder
        DatabaseReference auctionRef = FirebaseDatabase.getInstance().getReference().child("Auctions").child(model.getAuctionId());
        if (model.getStartDate() <= System.currentTimeMillis() && System.currentTimeMillis() <= model.getEndDate()) {
            model.setAuctionStatus("Live");
            auctionRef.child("auctionStatus").setValue("Live");
        }
        else if(model.getEndDate() <= System.currentTimeMillis()){
            model.setAuctionStatus("Completed");
            auctionRef.child("auctionStatus").setValue("Completed");
        }
        else{
            model.setAuctionStatus("Upcoming");
            auctionRef.child("auctionStatus").setValue("Upcoming");
        }

        switch (model.getAuctionStatus()) {
            case "Upcoming":
                holder.statusTextView.setTextColor(Color.parseColor("#FFA500"));

                break;
            case "Live":
                holder.statusTextView.setTextColor(Color.parseColor("#00FF22"));
                break;
            case "Completed":
                holder.statusTextView.setTextColor(Color.parseColor("#FF2200"));
                auctionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("currentBidder").exists()){
                            auctionRef.child("winnerId").setValue(snapshot.child("currentBidder").getValue(String.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + model.getAuctionStatus());
        }
        holder.titleTextView.setText(model.getTitle());
        holder.statusTextView.setText(model.getAuctionStatus());
        holder.startBidTextView.setText("Starting Bid: ₹" + model.getStartingBid());

        holder.sellerTextView.setText("Seller: " + model.getSellerId());
        loadImageUsingGlide(holder.imageView, model.getImageUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event, for example, start the AuctionViewActivity
                Context context = view.getContext();
                Intent intent = new Intent(context, AuctionViewActivity.class);

                // Pass data to the AuctionViewActivity
                intent.putExtra("auctionId",model.getAuctionId());

                context.startActivity(intent);
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, statusTextView, startBidTextView, sellerTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.auctionImageDisplayView);
            titleTextView = itemView.findViewById(R.id.titleDisplayView);
            statusTextView = itemView.findViewById(R.id.statusDisplayView);
            startBidTextView = itemView.findViewById(R.id.bidDisplayView);
            sellerTextView = itemView.findViewById(R.id.sellerDisplayView);
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
