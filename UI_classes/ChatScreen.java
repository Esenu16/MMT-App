package com.example.mmtapp.UI_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mmtapp.R;
import com.example.mmtapp.adapter_classes.ChatAdapter;
import com.example.mmtapp.model_classes.Doctor;
import com.example.mmtapp.model_classes.Message;
import com.example.mmtapp.model_classes.User;
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
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class ChatScreen extends AppCompatActivity {
    EditText messageEditText;
    ImageView sendButton;
    TextView friendNameView;
    ImageView phoneCallButton, videoCallButton;
    RecyclerView chatRecyclerView;
    LinearLayout friendProfilePicView;
    String messageContent;
    String time;
    String date;
    String receiverUID;
    String tel;
    DatabaseReference usersRef;
    StorageReference path;
    Message message;
    ArrayList<Message> chatData;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter adapter;
    String sender, friendUserUID, friendUserName, myUID, friendProfilePicUrl;
    Intent newChatIntent;
    Task<Uri> getImageDownloadUrl;
    String userType;
    Intent callIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        //Initialize variables
        messageEditText = findViewById(R.id.message_sendEditText);
        sendButton = findViewById(R.id.message_sendButton);
        chatRecyclerView = findViewById(R.id.chat_recyclerView);
        friendProfilePicView = findViewById(R.id.friend_profile_pic_imageView);
        friendNameView = findViewById(R.id.friend_name_textView);
        phoneCallButton = findViewById(R.id.phone_call_button);
        videoCallButton = findViewById(R.id.video_phone_call_button);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setHasFixedSize(true);

        message = new Message();
        chatData = new ArrayList<>();
        newChatIntent = getIntent();


        myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userType = Objects.requireNonNull(newChatIntent.getExtras()).getString("userType");
        assert userType != null;

        retrieveUserData();


        try {
            //Loading friends profile pic on chat screen
            friendUserName = Objects.requireNonNull(newChatIntent.getExtras()).getString("userName");
            friendUserUID = Objects.requireNonNull(newChatIntent.getExtras()).getString("userUID");
            friendNameView.setText(friendUserName);

            path = FirebaseStorage.getInstance().getReference();
            StorageReference imagePath = path.child(friendUserUID);
            getImageDownloadUrl = imagePath.getDownloadUrl();
            getImageDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    friendProfilePicUrl = uri.toString();
                    callIntent.putExtra("friendName", friendUserName);
                    callIntent.putExtra("friendPicUrl", friendProfilePicUrl);
                    Glide.with(getApplicationContext())
                            .load(friendProfilePicUrl)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    friendProfilePicView.setForeground(resource);
                                }
                            });
                }
            });
            callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
            //callIntent=new Intent(getApplicationContext(),PhoneCallScreen.class);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clearResponses();
                    Toast.makeText(getApplicationContext(), "Placing a call", Toast.LENGTH_SHORT).show();
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });


        videoCallButton.setOnClickListener(v -> {
            new Thread(this::clearResponses);
            Toast.makeText(getApplicationContext(), "Placing a video call", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),VideoCallScreen.class));
        });

        receiveMessages();

        Date d = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        @SuppressLint({"SimpleDateFormat", "WeekBasedYear"}) SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        date = dateFormat.format(d);
        time = timeFormat.format(d);
        receiverUID = friendUserUID;

        //Sending messages

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageContent = messageEditText.getText().toString().trim();
                if (messageContent.isEmpty()) {
                    messageEditText.requestFocus();
                    messageEditText.setError("Empty message");
                    return;
                }
                sendMessage();
            }
        });

    }

    private void clearResponses() {
        try {
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses")
                    .child(receiverUID).removeValue();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void receiveMessages() {
        try {
            DatabaseReference chatRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Chats");
            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        message = snapshot.getValue(Message.class);
                        assert message != null;

                        if (message.getSenderUID().equals(myUID) && message.getReceiverUID().equals(friendUserUID)) {
                            adapter = new ChatAdapter(chatData, getApplicationContext());
                            chatData.add(message);
                            adapter.notifyDataSetChanged();
                            chatRecyclerView.setAdapter(adapter);
                            chatRecyclerView.scrollToPosition(chatData.size()-1);

                        }
                        if (message.getSenderUID().equals(friendUserUID) && message.getReceiverUID().equals(myUID)) {
                            adapter = new ChatAdapter(chatData, getApplicationContext());
                            chatData.add(message);
                            adapter.notifyDataSetChanged();
                            chatRecyclerView.setAdapter(adapter);
                            chatRecyclerView.scrollToPosition(chatData.size()-1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (DatabaseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void sendMessage() {
        try {
            message.setContent(messageContent);
            message.setSenderUID(myUID);
            message.setDate(date);
            message.setTime(time);
            message.setReceiverUID(receiverUID);
            message.setMe(true);
            message.setSender(sender);
            message.setUserType(userType);
            DatabaseReference chatRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Chats");
            chatRef.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    messageEditText.setText("");
                    Toast.makeText(ChatScreen.this, "Message sent", Toast.LENGTH_SHORT).show();
                    receiveMessages();
                }
            });
        } catch (DatabaseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    void retrieveUserData(){

        if(userType.equals("patient")){
            tel= Objects.requireNonNull(newChatIntent.getExtras()).getString("docContact");

            try {
                usersRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users");
            } catch (Exception e) {
                e.printStackTrace();
            }

            usersRef.child(myUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    new User();
                    User myAccount;
                    myAccount = dataSnapshot.getValue(User.class);
                    assert myAccount != null;
                    sender = myAccount.getFirstName() + " " + myAccount.getSurname();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if(userType.equals("doctor")){
            tel= Objects.requireNonNull(newChatIntent.getExtras()).getString("Contact");
            try {
                usersRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors");

            } catch (Exception e) {
                e.printStackTrace();
            }

            usersRef.child(myUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    new User();
                    Doctor myAccount;
                    myAccount = dataSnapshot.getValue(Doctor.class);
                    assert myAccount != null;
                    sender = myAccount.getFirstName() + " " + myAccount.getSurname();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
