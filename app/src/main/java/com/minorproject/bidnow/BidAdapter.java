package com.minorproject.bidnow;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BidAdapter extends FirebaseRecyclerAdapter<Bid, BidAdapter.ViewHolder> {

    public BidAdapter(@NonNull FirebaseRecyclerOptions<Bid> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Bid model) {
        holder.amountTextView.setText( "₹" +(int) model.getBidAmount());
        FirebaseDatabase.getInstance().getReference("Users").child(model.getBidderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.bidderTextView.setText(snapshot.child("username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String formattedDate = convertLongToDateString(model.getBidTimestamp());
        holder.bidTimeTextView.setText(formattedDate);


    }
    public String convertLongToDateString(long timestamp) {
        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Convert the timestamp to a Date object
        Date date = new Date(timestamp);

        // Format the date as a string
        return dateFormat.format(date);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_list_in_auction_view, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, bidderTextView, bidTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.bidAmountTextView);
            bidderTextView = itemView.findViewById(R.id.bidderNameTextView);
            bidTimeTextView = itemView.findViewById(R.id.bidTimeTextView);
        }
    }
}

