package com.minorproject.bidnow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextView loginTextView;
    EditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText,phoneEditText;
    String username,email,password,confirmPassword,phone;
    Button registerButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginTextView = findViewById(R.id.loginTextView);

        emailEditText = findViewById(R.id.emailRegiterEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordRegisterEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordRegisterEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = usernameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                confirmPassword = confirmPasswordEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();

                register();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });
    }

    private void register() {

        if(username.isEmpty()){
            usernameEditText.setError("Username cannot be empty!");
            usernameEditText.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEditText.setError("Email cannot be empty!");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Enter valid email address");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordEditText.setError("Password cannot be empty!");
            passwordEditText.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty()){
            confirmPasswordEditText.setError("Confirm Password cannot be empty!");
            confirmPasswordEditText.requestFocus();
            return;
        }
        if(!confirmPassword.equals(password)){
            confirmPasswordEditText.setError("Confirm Password is not matching with Password");
            confirmPasswordEditText.requestFocus();
            return;
        }

        String regexPattern = "^[1-9]\\d{9}$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(phone);

        if (!matcher.matches()) {
            phoneEditText.setError("Enter valid phone no.");
            phoneEditText.requestFocus();
            return;
        }

        User user = new User(username,email,password,phone);


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String childPath = mAuth.getCurrentUser().getUid().toString();
                    databaseReference.child(childPath).setValue(user);
                    Toast.makeText(RegisterActivity.this,"Registration Successful, Login to continue",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Registration Unsuccessful",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
