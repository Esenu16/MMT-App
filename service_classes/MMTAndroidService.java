package com.example.mmtapp.service_classes;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mmtapp.R;
import com.example.mmtapp.doctor_classes.DoctorsNavigationScreen;
import com.example.mmtapp.model_classes.Message;
import com.example.mmtapp.patient_classes.PatientsNavigationScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;


public class MMTAndroidService extends Service {

    public static final String CHANNEL_ID = "MMTChannel";
    String myUID, messageContent, senderName,senderUID;
    DatabaseReference chatRef;
    Message message;
    Task<Uri>getUrl;
    String largeIconUrl;
    NotificationReceiver notificationReceiver;
    Intent bindIntent;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        myUID= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        message = new Message();
        IntentFilter filter = new IntentFilter(USER_SERVICE);
        notificationReceiver=new NotificationReceiver();
        registerReceiver(notificationReceiver,filter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {

        try{
            chatRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Chats");
            retrieveFirebaseData();
            //sendBroadcast(bindIntent);
        } catch(DatabaseException e){
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(final @NonNull Intent intent) {
        return null;
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

    @RequiresApi(api = Build.VERSION_CODES.N)

  private class NotificationReceiver extends BroadcastReceiver{

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {

            String messageContent=intent.getStringExtra("message");
            String title=intent.getStringExtra("sender");
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.pharma)
                    .setContentTitle(senderName)
                    .setContentText(messageContent)
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

    void showNotifications(){
        Intent intent=new Intent();
        String messageContent=intent.getStringExtra("message");
        String title=intent.getStringExtra("sender");
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,bindIntent,0);
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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