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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityAuctionAdapter extends RecyclerView.Adapter<ActivityAuctionAdapter.ViewHolder> {
    private ArrayList<Auction> auctionList;
    private Context context;
    private String mobile;

    public ActivityAuctionAdapter(Context context, ArrayList<Auction> auctionList) {
        this.context = context;
        this.auctionList = auctionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_auction_live, parent, false);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_auction_live,parent,false);
        ActivityAuctionAdapter.ViewHolder viewHolder = new ActivityAuctionAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAuctionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Auction model = auctionList.get(position);

        // settexts to be filled

        // Bind the data to the views in the ViewHolder
        DatabaseReference auctionRef = FirebaseDatabase.getInstance().getReference().child("Auctions").child(model.getAuctionId());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        auctionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Auction model = snapshot.getValue(Auction.class);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
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
                                holder.contactWinner.setEnabled(false);
                                holder.declareWinner.setEnabled(false);
                                break;
                            case "Live":
                                holder.statusTextView.setTextColor(Color.parseColor("#00FF22"));
                                holder.contactWinner.setEnabled(false);
                                if(String.valueOf(model.getStartingBid()).equals(String.valueOf(model.getCurrentBid()))){
                                    holder.declareWinner.setEnabled(false);
                                }
                                break;
                            case "Completed":
                                holder.statusTextView.setTextColor(Color.parseColor("#FF2200"));
                                holder.declareWinner.setEnabled(false);
                                if(String.valueOf(model.getStartingBid()).equals(String.valueOf(model.getCurrentBid()))){
                                    holder.contactWinner.setEnabled(false);
                                }
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + model.getAuctionStatus());
                        }
                        holder.titleTextView.setText(model.getTitle());
                        holder.statusTextView.setText(model.getAuctionStatus());
                        holder.currentBidTextView.setText("Current Bid: ₹" + model.getCurrentBid());

                        holder.currentBidderTextView.setText("Current Bidder: " + userSnapshot.child("username").getValue(String.class));
                        loadImageUsingGlide(holder.imageView, model.getImageUrl());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.deleteAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auctionRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Auction Deleted Succesfully!!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Auction cannot be Deleted!!!", Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("CreatedAuctions")
                                                .child(model.getAuctionId())
                                                        .removeValue();
                auctionList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.declareWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auctionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    private final String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        auctionRef.child("endDate").setValue(snapshot.child("startDate").getValue(Long.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.currentBidTextView.setText("Final Bid: ₹" + model.getCurrentBid());

                                holder.currentBidderTextView.setText("Final Bidder: " + model.getWinnerId());
                                Toast.makeText(context, "Winner Declared", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Unable to declare winner", Toast.LENGTH_SHORT).show();
                            }
                        });
                        auctionRef.child("winnerId").setValue(userId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.contactWinner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                auctionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("winnerId")){
                            String winnerBidder = snapshot.child("winnerId").getValue(String.class);
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("winnerId");

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    String mobile = userSnapshot.child("phone").getValue(String.class);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91" + mobile));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
