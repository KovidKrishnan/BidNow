package com.minorproject.bidnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private TextView logoutButton;
    private DrawerLayout drawerLayout;
    boolean backPressedOnce = false;
    TextView username,creditValue;
    DatabaseReference databaseReference;
    FirebaseUser userFirebase;
    String usernameString,creditValueString;
    RelativeLayout auctionsCard,createAuctionCard;
    RelativeLayout myActivityCard;
    ImageView instagram, linkedIn, twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);

        mAuth = FirebaseAuth.getInstance();
        userFirebase = mAuth.getCurrentUser();
        username = findViewById(R.id.usernameTextView);
        creditValue = findViewById(R.id.creditValueTextView);
        myActivityCard = findViewById(R.id.myActivityCard);

        instagram = findViewById(R.id.instagramLogo);
        linkedIn = findViewById(R.id.linkedinLogo);
        twitter = findViewById(R.id.twitterlogo);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        createAuctionCard = findViewById(R.id.createAuctionCard);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid().toString());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usernameString = snapshot.child("username").getValue(String.class);
                creditValueString = snapshot.child("creditValue").getValue(Integer.class).toString();
                creditValue.setText(creditValueString);
                username.setText(usernameString);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        auctionsCard = findViewById(R.id.auctionsCard);
        auctionsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AuctionListingActivity.class));
            }
        });

        createAuctionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,CreateAuctionActivity.class));
            }
        });

        ImageView profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        logoutButton.setOnClickListener(view -> showSignOutDialog());

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/winter_phile/")));
            }
        });
        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/kovidkrishnan")));
            }
        });
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_sign_out, null);

        // Set the custom view for the dialog
        builder.setView(dialogView);

        // Build the dialog
        AlertDialog dialog = builder.create();

        // Retrieve the buttons from the custom layout
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        // Set button click listeners
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign out action here
                // For example, you can start the sign out process

                // Dismiss the dialog
                signOut();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog without any further action
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
    private void signOut() {
        mAuth.signOut();
        Toast.makeText(HomeActivity.this,"Thanks for your time here!, Come back again!",Toast.LENGTH_LONG).show();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));

    }
    @Override
    public void onBackPressed() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (backPressedOnce) {
                // User is authenticated and back button has been pressed twice
                finishAffinity(); // Close the application
            } else {
                backPressedOnce = true;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> backPressedOnce = false, 2000); // Reset after 2 seconds
            }
        } else {
            super.onBackPressed(); // Default behavior
        }
    }


    public void onUpdateProfileClick(View view) {
        startActivity(new Intent(HomeActivity.this, UpdateProfileActivity.class));
    }
}