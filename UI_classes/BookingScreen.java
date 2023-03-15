package com.example.mmtapp.UI_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mmtapp.R;
import com.example.mmtapp.model_classes.BookingResponse;
import com.example.mmtapp.model_classes.Doctor;
import com.example.mmtapp.model_classes.User;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class BookingScreen extends AppCompatActivity {
    LinearLayout appointmentButton;
    String patientName, patientUID, sender, myUID, date, time,patientProfilePic,doctorProfilePicUrl,doctorContact,patientContact;
    DatabaseReference responseRef, doctorsRef;
    Intent responseIntent;
    Intent chatIntent;
    Doctor doctor;
    Task<Uri>getUrl;
    StorageReference path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookings_screen);
        appointmentButton = findViewById(R.id.calendarAppointmentButton);
        setAppBarColor();


        responseIntent = getIntent();
        chatIntent = new Intent(getApplicationContext(), ChatScreen.class);
        patientUID = Objects.requireNonNull(responseIntent.getExtras()).getString("patientUID");
        patientName = responseIntent.getExtras().getString("patientName");
        patientContact=responseIntent.getExtras().getString("Contact");
        //From one activity to another, we pass the values

        myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        Date d = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        @SuppressLint({"SimpleDateFormat", "WeekBasedYear"}) SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        date = dateFormat.format(d);
        time = timeFormat.format(d);

        retrieveUsers();
        retrievePatients();
        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIntent.putExtra("userName", patientName);
                chatIntent.putExtra("userUID", patientUID);
                chatIntent.putExtra("Contact",patientContact);
                chatIntent.putExtra("docContact",doctorContact);
                String userType="doctor";
                chatIntent.putExtra("userType",userType);
                sendChatInfo();
                saveToContactHistory();
            }
        });
    }

    void retrieveUsers() {
        try {
            doctorsRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors");
        doctorsRef.child(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                doctor = dataSnapshot.getValue(Doctor.class);
                assert doctor != null;
                sender = doctor.getFirstName() + " " + doctor.getSurname();
                doctorProfilePicUrl=doctor.getProfilePicUrl();
                doctorContact=doctor.getContact();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    void sendChatInfo() {
        BookingResponse response = new BookingResponse();
        response.setPatientName(patientName);
        response.setDoctorName(sender);
        response.setDoctorUID(myUID);
        response.setPatientUID(patientUID);
        response.setResponseDate(date);
        response.setResponseTime(time);
        response.setPatientProfilePicUrl(patientProfilePic);
        response.setDoctorProfilePicUrl(doctorProfilePicUrl);
        response.setDoctorContact(doctorContact);
        response.setPatientContact(patientContact);

        try {
            responseRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses");
            responseRef.child(patientUID).setValue(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(chatIntent));
                    Toast.makeText(getApplicationContext(), "Welcome Doctor", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private void setAppBarColor() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#3C6688"));
    }
    private void saveToContactHistory() {
        BookingResponse response = new BookingResponse();
        response.setPatientName(patientName);
        response.setDoctorName(sender);
        response.setDoctorUID(myUID);
        response.setPatientUID(patientUID);
        response.setResponseDate(date);
        response.setResponseTime(time);
        response.setPatientProfilePicUrl(patientProfilePic);
        response.setDoctorProfilePicUrl(doctorProfilePicUrl);
        response.setDoctorContact(doctorContact);
        response.setPatientContact(patientContact);

        try {
            DatabaseReference contactRef=FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Contacts");
            contactRef.child(patientUID).setValue(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    void retrievePatients() {
        try {
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users")
            .child(patientUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    patientProfilePic = user.getProfilePicUrl();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
