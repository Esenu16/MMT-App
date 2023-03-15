package com.example.mmtapp.UI_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mmtapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class SignUpScreen extends AppCompatActivity {
    EditText signUp_emailText, signUp_passwordText, confirmText;
    ProgressBar signUp_progress;
    LinearLayout signUpButton;
    String userEmail, userPassword, confirmedPassword;
    FirebaseUser user;
    CheckBox checkBox;
    FirebaseAuth mAuth;


    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);

        //initialize views
        signUp_emailText = findViewById(R.id.sign_up_email_edit_text);
        signUp_passwordText = findViewById(R.id.sign_up_password_edit_text);
        confirmText = findViewById(R.id.sign_up_confirm_edit_text);
        signUp_progress = findViewById(R.id.signUp_progressBar);
        signUpButton = findViewById(R.id.signUpButton);
        checkBox=findViewById(R.id.checkBox);

        //Setting on click listeners

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    signUpButton.setBackground(getDrawable(R.drawable.log_in_button_bg));
                } else {
                    signUpButton.setBackground(getDrawable(R.drawable.button_null_state));
                }
            }
        });

        FirebaseApp.initializeApp(getApplicationContext());

        //Initialising the authentication client for the application
        try{
            mAuth=FirebaseAuth.getInstance();
        } catch (Exception e){
            e.printStackTrace();
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onClick(View v) {
                //get the texts that has been entered in the text fields
                userEmail = signUp_emailText.getText().toString().trim();
                userPassword = signUp_passwordText.getText().toString().trim();
                confirmedPassword = confirmText.getText().toString().trim();

                //Perform validations
                if (userEmail.isEmpty()) {
                    signUp_emailText.setError("email required");
                    signUp_emailText.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    signUp_emailText.setError("please provide a valid email");
                    signUp_emailText.requestFocus();
                    return;
                }
                if (userPassword.isEmpty()) {
                    signUp_passwordText.setError("password required");
                    signUp_passwordText.requestFocus();
                    return;
                }
                if (userPassword.length() < 8) {
                    signUp_passwordText.setError("password must be at least 8 characters");
                    signUp_passwordText.requestFocus();
                    return;
                }
                if (confirmedPassword.isEmpty()) {
                    confirmText.setError("Password cannot be empty");
                    confirmText.requestFocus();
                    return;
                }
                if (confirmedPassword.length() < 8) {
                    confirmText.setError("password must be at least 8 characters");
                    confirmText.requestFocus();
                    return;
                }
                if (!confirmedPassword.equals(userPassword)) {
                    confirmText.setError("Password mismatch");
                    confirmText.requestFocus();
                    return;
                }
                if(!checkBox.isChecked()) {
                    checkBox.setTextColor(Color.parseColor("#E30929"));
                    return;
                }
                    signUp_progress.setVisibility(View.VISIBLE);
                    registerUser(userEmail, confirmedPassword);

            }
        });
    }

    private void registerUser(final String userEmail, final String confirmedPassword) {

        try {
            mAuth.createUserWithEmailAndPassword(userEmail, confirmedPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        signUp_progress.setVisibility(View.GONE);
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;

                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "An email verification link has been sent to your email", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Check your spam folder if you do not see the email verification link", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                                finish();
                            }
                        });

                    } else {
                        signUp_progress.setVisibility(View.GONE);
                        String error=Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(SignUpScreen.this,error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}