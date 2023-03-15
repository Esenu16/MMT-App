package com.example.mmtapp.patient_classes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.ChatScreen;
import com.example.mmtapp.UI_classes.LoginScreen;
import com.example.mmtapp.UI_classes.ProfileUploadScreen;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.adapter_classes.DoctorAdapter;
import com.example.mmtapp.model_classes.BookingRequest;
import com.example.mmtapp.model_classes.Doctor;
import com.example.mmtapp.model_classes.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    RecyclerView doctorRecyclerView;
    DoctorAdapter doctorAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Doctor> doctorList;
    CardView signOutButton;
    TextView currentDoctorsTV;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference docRef,bookingsRef,usersRef;
    Doctor doctor;
    LottieAnimationView loadingProgressLottie;
    String myUID, receiverUID, profilePicUrl, receiverFullName,sender,doctorContact,patientContact;
    LinearLayout profilePicContainer;
    Intent chatIntent;
    BookingRequest request;
    TextView myAccountTextView;


    public HomeFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        doctorRecyclerView = view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        currentDoctorsTV = view.findViewById(R.id.current_doctors_textView);
        currentDoctorsTV.setTextColor(Color.RED);
        signOutButton = view.findViewById(R.id.sign_out_button);
        profilePicContainer = view.findViewById(R.id.patient_profile_pic_container);
        loadingProgressLottie = view.findViewById(R.id.loading_progressLottie);
        myAccountTextView=view.findViewById(R.id.my_account_TextView);
        loadingProgressLottie.setVisibility(View.VISIBLE);

        FirebaseApp.initializeApp(requireContext());
        doctorList = new ArrayList<>();
        chatIntent = new Intent(requireContext(), ChatScreen.class);
        request=new BookingRequest();


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        myUID = mAuth.getUid();

        doctorRecyclerView.setLayoutManager(linearLayoutManager);
        doctorRecyclerView.setHasFixedSize(true);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(requireActivity(), LoginScreen.class));
                Toast.makeText(getContext(), "You have been signed out", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        profilePicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileUploadScreen.class));
            }
        });
        loadProfilePic();
        retrieveMyData();
        retrieveRegisteredDoctors();
        return view;
    }



    void retrieveRegisteredDoctors() {
        //fetch the data for the doctors
        try {
            docRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors");
            docRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String currentDoctors = String.valueOf(dataSnapshot.getChildrenCount());
                    if (currentDoctors.equals("1")) {
                        currentDoctorsTV.setText(currentDoctors + " doctor");
                    } else {
                        currentDoctorsTV.setText(currentDoctors + " doctors");
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        doctor = snapshot.getValue(Doctor.class);
                        if (doctor != null) {
                            doctorList.add(doctor);
                            doctorAdapter = new DoctorAdapter(doctorList, requireContext(), new RecyclerViewInterface() {
                                @Override
                                public void onClick(int position) {
                                    receiverUID = doctorList.get(position).getUserUID();
                                    doctorContact = doctorList.get(position).getContact();
                                    receiverFullName = doctorList.get(position).getFirstName() + " " + doctorList.get(position).getSurname();
                                    String userType = "patient";
                                    chatIntent.putExtra("userName", receiverFullName);
                                    chatIntent.putExtra("userUID", receiverUID);
                                    chatIntent.putExtra("docContact", doctorContact);
                                    chatIntent.putExtra("Contact", patientContact);
                                    chatIntent.putExtra("userType", userType);


                                    //Builder call back
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                                    LayoutInflater snackInflater = requireActivity().getLayoutInflater();
                                    @SuppressLint("InflateParams") View view = snackInflater.inflate(R.layout.share_layout, null);
                                    builder.setView(view)
                                            .setTitle("MMT action required")
                                            .setPositiveButton("Chat", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startActivity(chatIntent);
                                                }
                                            }).setNegativeButton("Make Appointment", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            makeAppointment();
                                        }
                                    });
                                    builder.create().show();
                                }
                            });
                            doctorAdapter.notifyDataSetChanged();
                            doctorRecyclerView.setAdapter(doctorAdapter);
                            loadingProgressLottie.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void finish() {
        requireActivity().finish();
    }

    void loadProfilePic() {
        try {
            usersRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users");
            usersRef.child(myUID).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        profilePicUrl = user.getProfilePicUrl();
                        sender = user.getFirstName() + " " + user.getSurname();
                        Glide.with(requireContext())
                                .load(profilePicUrl)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        profilePicContainer.setForeground(resource);
                                    }
                                });

                    } else {
                        myAccountTextView.setText("Upload Info");
                        myAccountTextView.setTextColor(Color.parseColor("#E30929"));
                        Toast.makeText(getContext(), "Click on my account to update your profile info", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception e){
            Toast.makeText(getContext(), "Please update your profile information", Toast.LENGTH_LONG).show();
        }
    }

     void makeAppointment() {
         request.setDoctorUID(receiverUID);
         request.setPatientUID(mUser.getUid());
         request.setPatientName(sender);
         request.setProfilePicUrl(profilePicUrl);
         request.setPatientContact(patientContact);

         try {
             bookingsRef= FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Requests");
             bookingsRef.child(receiverUID).setValue(request).addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Your request has been sent to the doctor", Toast.LENGTH_LONG).show());
         } catch (Exception e){
             e.printStackTrace();
         }
    }
    void retrieveMyData(){
        try{
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users").
                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                patientContact = user.getContact();
                            }
                            else {
                                patientContact=null;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch(Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}