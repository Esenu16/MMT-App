package com.example.mmtapp.UI_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mmtapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetScreen extends AppCompatActivity {
    EditText reset_emailText;
    Button resetButton;
    String userEmail;
    ProgressBar resetProgress;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset_screen);
        reset_emailText = findViewById(R.id.reset_email_edit_text);
        resetButton = findViewById(R.id.reset_button);
        resetProgress = findViewById(R.id.reset_progressBar);
        FirebaseApp.initializeApp(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = reset_emailText.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    reset_emailText.setError("provide email");
                    reset_emailText.requestFocus();
                    return;
                }
                resetProgress.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            resetProgress.setVisibility(View.GONE);
                           startActivity(new Intent(getApplicationContext(), ResetMessage.class));
                           finish();
                        } else {
                            Toast.makeText(PasswordResetScreen.this, "could not send link try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}