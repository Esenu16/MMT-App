package com.example.mmtapp.patient_classes;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mmtapp.R;
import com.example.mmtapp.doctor_classes.DoctorsNavigationScreen;
import com.example.mmtapp.model_classes.BookingResponse;
import com.example.mmtapp.model_classes.Message;
import com.example.mmtapp.service_classes.MMTAndroidService;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;


public class PatientsNavigationScreen extends AppCompatActivity {
    BottomNavigationView navigationView;
    int count = 2;
    int badgeCount;
    String myUID;
    BadgeDrawable badge;
    Handler handler;

    public static final String CHANNEL_ID = "MMTChannel";
    String messageContent, senderName,senderUID;
    DatabaseReference chatRef;
    Message message;
    Task<Uri> getUrl;
    String largeIconUrl;
    Intent bindIntent;
    NotificationManager manager;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patients_nav_screen);

        setAppBarColor();
        handler=new Handler();
        myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try{
            chatRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Chats");
            retrieveFirebaseData();
            //sendBroadcast(bindIntent);
        } catch(DatabaseException e){
            e.printStackTrace();
        }


        navigationView = findViewById(R.id.bottom_nav_view);
        replaceFragment(new HomeFragment());

        badge = navigationView.getOrCreateBadge(R.id.notificationFragment);
        badge.setBackgroundColor(Color.RED);
        badge.setBadgeTextColor(Color.WHITE);
        getBadgeCount();

        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeFragment:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.appointmentFragment:
                    replaceFragment(new AppointmentFragment());
                    break;
                case R.id.notificationFragment:
                    replaceFragment(new NotificationFragment());
                    break;

            }
            return true;
        });
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_window, fragment);
        fragmentTransaction.commit();
    }

    private void setAppBarColor() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#5085AF"));
    }

    @Override
    public void onBackPressed() {
        count -= 1;
       handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count=2;
            }
        },4000);
        if (count == 0) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Press again to exit the application", Toast.LENGTH_SHORT).show();
        }
    }

    private void startServiceDaemon() {
      /* Intent intent = new Intent(getApplicationContext(), MMTAndroidService.class);
        startService(intent);
        Toast.makeText(getApplicationContext(), "MMT Background Service started", Toast.LENGTH_SHORT).show(); */
      new Thread(new Runnable() {
          @Override
          public void run() {
          }
      }).start();
    }

    public void getBadgeCount() {
        try {
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses")
                    .child(myUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    badgeCount = (int) dataSnapshot.getChildrenCount();
                        BookingResponse response = dataSnapshot.getValue(BookingResponse.class);
                        if (response != null) {
                            badge.setVisible(true);
                            badge.setNumber(1);
                        } else {
                            badge.setVisible(false);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void retrieveFirebaseData() {

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ServiceCast")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    message = messageSnapshot.getValue(Message.class);
                    assert message != null;
                    senderName = message.getSender();
                    senderUID=message.getSenderUID();
                    messageContent = message.getContent();

                    if(message.getUserType().equals("patient")){
                        bindIntent=new Intent(getApplicationContext(), PatientsNavigationScreen.class);
                        bindIntent.putExtra("message",messageContent);
                        bindIntent.putExtra("sender",senderName);
                    } else {
                        bindIntent=new Intent(getApplicationContext(), DoctorsNavigationScreen.class);
                        bindIntent.putExtra("message",messageContent);
                        bindIntent.putExtra("sender",senderName);
                    }
                    if ( message.getReceiverUID().equals(myUID)) {
                        getUrl = FirebaseStorage.getInstance().getReference(message.getSenderUID()).getDownloadUrl();
                        getUrl.addOnSuccessListener(uri -> largeIconUrl=uri.toString());
                        showNotifications();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void showNotifications(){
        Intent intent=new Intent();
        String messageContent=intent.getStringExtra("message");
        String title=intent.getStringExtra("sender");
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,bindIntent,0);
        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.pharma)
                .setContentTitle(message.getSender())
                .setContentText(message.getContent())
                .setShowWhen(false)
                .setDefaults(NotificationManager.IMPORTANCE_HIGH)
                .setPriority(NotificationManager.BUBBLE_PREFERENCE_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent,true)
                .setVibrate(new long[]{500, 500})
                .build();
        manager.notify(1, notification);
    }
}