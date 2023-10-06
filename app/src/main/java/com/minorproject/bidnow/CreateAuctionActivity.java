package com.minorproject.bidnow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateAuctionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText editTitle, editDescription, editStartingBid;
    Button btnStartDate,btnEndDate,btnCreateAuction,btnUploadImage;
    private ImageView imageAuction;
    private Uri selectedImageUri;
    private Uri uploadedImageUri;
    private Calendar startDateTime = Calendar.getInstance();
    private Calendar endDateTime = Calendar.getInstance();

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String imageUrl;
    private String username;

    private FirebaseAuth mAuth;
    boolean isStartDateSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_auction);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Auctions/").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + System.currentTimeMillis());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Auctions");

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editStartingBid = findViewById(R.id.editStartingBid);

        imageAuction = findViewById(R.id.imageAuction);
        imageAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnCreateAuction = findViewById(R.id.btnCreateAuction);

        btnCreateAuction.setEnabled(false);


        btnEndDate.setEnabled(false);
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(startDateTime);

                if(isStartDateSet)
                    btnEndDate.setEnabled(true);
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStartDateSet) {
                    Toast.makeText(CreateAuctionActivity.this, "Set the start date and time first.", Toast.LENGTH_SHORT).show();
                } else {
                    showDateTimePicker(endDateTime, startDateTime);
                }
            }
        });


        btnCreateAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String description = editDescription.getText().toString();
                String startingBidStr = editStartingBid.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                        TextUtils.isEmpty(startingBidStr) || selectedImageUri == null) {
                    Toast.makeText(CreateAuctionActivity.this, "Please fill in all fields and select an image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sellerId =  Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                String auctionId = sellerId + System.currentTimeMillis();

                long startTimeInMillis = startDateTime.getTimeInMillis(), endTimeInMillis = endDateTime.getTimeInMillis();
                double startingBid = Double.parseDouble(startingBidStr);



                username = mAuth.getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String publicName = snapshot.child("username").getValue(String.class);
                        Auction auction = new Auction(auctionId, title, description, startingBid, imageUrl,publicName,"Upcoming",startTimeInMillis,endTimeInMillis);
                        databaseReference = databaseReference.child(auctionId);

                        saveAuctionDataToDatabase(auction);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    private void uploadImagetoStorage(Uri selectedImageUri, StorageReference storageReference) {
        UploadTask uploadTask = storageReference.putFile(selectedImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads (e.g., get the download URL)
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        uploadedImageUri = downloadUri;
                        imageUrl = uploadedImageUri.toString();
                        Toast.makeText(CreateAuctionActivity.this,"Image uploaded to storage!!",Toast.LENGTH_SHORT).show();
                        btnCreateAuction.setEnabled(true);
                        // Now you can use imageUrl to display the uploaded image or store its reference in your database.
                    }
                });
            }
        });
    }

    // Call this method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Set the selected image to the ImageView
            imageAuction.setImageURI(selectedImageUri);
            uploadImagetoStorage(selectedImageUri,storageReference);
        }
    }

    private void showDateTimePicker(final Calendar dateTime) {
        int year = dateTime.get(Calendar.YEAR);
        int month = dateTime.get(Calendar.MONTH);
        int day = dateTime.get(Calendar.DAY_OF_MONTH);
        int hour = dateTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateTime.get(Calendar.MINUTE);

        // Calculate the minimum start time (10 minutes after the current time)
        Calendar minStartTime = Calendar.getInstance();
        minStartTime.add(Calendar.MINUTE, 10);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAuctionActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateTime.set(Calendar.YEAR, year);
                        startDateTime.set(Calendar.MONTH, monthOfYear);
                        startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAuctionActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        startDateTime.set(Calendar.MINUTE, minute);

                                        // Check if selected date and time is at least 10 minutes after the current time
                                        if (startDateTime.after(minStartTime)) {
                                            updateButtonText(startDateTime);

                                            // Set the flag to indicate that the start date is set
                                            isStartDateSet = true;

                                            // Enable the End Date button
                                            btnEndDate.setEnabled(true);
                                        } else {
                                            // Display an error message or handle the case where start time is too soon
                                            Toast.makeText(CreateAuctionActivity.this, "Start time must be at least 10 minutes from now", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, hour, minute, false);
                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }


    private void showDateTimePicker(final Calendar dateTime, final Calendar minStartDateTime) {
        int year = dateTime.get(Calendar.YEAR);
        int month = dateTime.get(Calendar.MONTH);
        int day = dateTime.get(Calendar.DAY_OF_MONTH);
        int hour = dateTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateTime.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAuctionActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDateTime.set(Calendar.YEAR, year);
                        endDateTime.set(Calendar.MONTH, monthOfYear);
                        endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);



                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAuctionActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        endDateTime.set(Calendar.MINUTE, minute);

                                        updateButtonText(endDateTime);
                                    }
                                }, hour, minute, false);



                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minStartDateTime.getTimeInMillis()+86400000);
        datePickerDialog.show();
    }


    private void updateButtonText(Calendar dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String formattedDateTime = sdf.format(dateTime.getTime());

        if (dateTime == startDateTime) {
            btnStartDate.setText(formattedDateTime);
        } else if (dateTime == endDateTime) {
            btnEndDate.setText(formattedDateTime);
        }
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_exit, null);

        // Set the custom view for the dialog
        builder.setView(dialogView);

        // Build the dialog
        final AlertDialog dialog = builder.create();

        // Retrieve the buttons from the custom layout
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        // Set button click listeners
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the app when "Yes" is clicked
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when "No" is clicked
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
    private void saveAuctionDataToDatabase(Auction auction) {
        // Implement the code to save auction data to your database (e.g., Firebase)
        // databaseReference.child("auctions").push().setValue(auction);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("CreatedAuctions").child(auction.getAuctionId());
        userRef.setValue(auction.getAuctionId());

        databaseReference.setValue(auction).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreateAuctionActivity.this, "Auction Created Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAuctionActivity.this,HomeActivity.class));
                }
                else{
                    Toast.makeText(CreateAuctionActivity.this, "Unable to Create Auction Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}