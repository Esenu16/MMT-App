package com.example.mmtapp.UI_classes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mmtapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class SplashScreen extends AppCompatActivity {
    LottieAnimationView splashLottie;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        setAppBarColor();
        splashLottie = findViewById(R.id.splash_progressLottie);
        splashLottie.setVisibility(View.VISIBLE);
        currentUser=null;

        FirebaseApp.initializeApp(getApplicationContext());

        Handler handler=new Handler();
        handler.postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(),LoginScreen.class));
                finish();
        },4000);
    }
    private void setAppBarColor() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
    }
}