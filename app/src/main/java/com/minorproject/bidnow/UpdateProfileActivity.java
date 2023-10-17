package com.minorproject.bidnow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private Button saveImage, saveButton;
    private EditText editName;

    FirebaseStorage storage;
    StorageReference profilePicRef;

    DatabaseReference userRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        profilePic = findViewById(R.id.profileImage);
        saveImage = findViewById(R.id.changeImageButton);
        editName = findViewById(R.id.editName);
        saveButton = findViewById(R.id.saveButton);

        storage = FirebaseStorage.getInstance().getReference("Profiles/").getStorage();
        profilePicRef = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
    }
}