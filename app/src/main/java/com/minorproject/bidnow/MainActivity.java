package com.minorproject.bidnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText loginEmailEditText, loginPasswordEditText;
    Button loginButton;
    String loginEmail, loginPassword;
    TextView registerTextview;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_login_layout);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }

        loginEmailEditText = findViewById(R.id.emailRegiterEditText);
        loginPasswordEditText = findViewById(R.id.passwordRegisterEditText);

        loginButton = findViewById(R.id.loginButton);
        registerTextview = findViewById(R.id.redirectRegisterEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail = loginEmailEditText.getText().toString().trim();
                loginPassword = loginPasswordEditText.getText().toString().trim();

                if(loginEmail.isEmpty()){
                    loginEmailEditText.setError("Email cannot be empty!!!");
                    loginEmailEditText.requestFocus();
                    return;
                }
                if(loginPassword.isEmpty()){
                    loginPasswordEditText.setError("Email cannot be empty!!!");
                    loginPasswordEditText.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
                    loginEmailEditText.setError("Enter valid email address");
                    loginEmailEditText.requestFocus();
                    return;
                }
                signIn(loginEmail,loginPassword);

            }

            private void signIn(String loginEmail, String loginPassword) {
                mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(mAuth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(MainActivity.this,"Login Successful!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                showNotification(MainActivity.this);
                            }
                            else{
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(tasks -> {
                                            if (tasks.isSuccessful()) {
                                                Toast.makeText(MainActivity.this,"Check your email for verification!!!",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.this,"Error occured while Logging in!!!",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Login unsuccessful!!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            private void showNotification(Context context) {
                // Create a NotificationCompat.Builder
                createNotificationChannel(context);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable._6681141368)
                        .setContentTitle("My Notification")
                        .setContentText("This is a notification example")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // Create a unique notification ID
                int notificationId = 1;

                // Build the notification
                Notification notification = builder.build();

                // Get the NotificationManager
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Show the notification
                notificationManager.notify(notificationId, notification);
            }
            private void createNotificationChannel(Context context) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "My Notification Channel";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
                    channel.setDescription(description);

                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }


        });

        registerTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

    }
    @Override
    public void onBackPressed() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            finishAffinity(); // Close the application
        } else {
            super.onBackPressed(); // Default behavior
        }
    }

}