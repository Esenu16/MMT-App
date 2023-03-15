package com.example.mmtapp.doctor_classes;

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
import com.example.mmtapp.UI_classes.LoginScreen;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.adapter_classes.PatientAdapter;
import com.example.mmtapp.model_classes.AppointmentParcel;
import com.example.mmtapp.model_classes.Doctor;
import com.example.mmtapp.model_classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ClinicFragment extends Fragment {
    RecyclerView patientRecyclerView;
    PatientAdapter patientAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<User> patientData;
    CardView signOutButton;
    LottieAnimationView loadingProgressLottie;
    FirebaseAuth mAuth;
    DatabaseReference usersRef,docRef;
    TextView currentUsersTextView;
    LinearLayout doctorProfilePicImageView;
    String profilePicUrl, myUID, sender;
    User user;
    AppointmentParcel parcel;
    Intent chatIntent;
    TextView docAccountTextView,docAccountTextIndicator;


    public ClinicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clinic, container, false);
        patientRecyclerView = v.findViewById(R.id.patients_recycler_view);
        signOutButton = v.findViewById(R.id.doctor_sign_out_button);
        doctorProfilePicImageView = v.findViewById(R.id.doctor_profile_pic_imageView);
        currentUsersTextView = v.findViewById(R.id.current_users_textView);
        loadingProgressLottie = v.findViewById(R.id.doctor_loading_progressLottie);
        docAccountTextView=v.findViewById(R.id.doc_account_textView);
        docAccountTextIndicator=v.findViewById(R.id.doc_account_textIndicator);

        loadingProgressLottie.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        patientRecyclerView.setLayoutManager(layoutManager);
        patientRecyclerView.setHasFixedSize(true);

        parcel = new AppointmentParcel();
        patientData = new ArrayList<>();

        //Create dialog box

        mAuth = FirebaseAuth.getInstance();
        myUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //call initialization methods

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(requireContext(), "You have been signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginScreen.class));
                finish();
            }
        });

        doctorProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),LicenseRegistrationScreen.class));
            }
        });
        loadProfilePic();
        retrievePatientData();
        return v;
    }

    private void retrievePatientData() {
                try {
                    usersRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users");
                    usersRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String currentUsers = String.valueOf(dataSnapshot.getChildrenCount());
                            currentUsersTextView.setText(currentUsers);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                user = snapshot.getValue(User.class);
                                patientData.add(user);
                                patientAdapter = new PatientAdapter(patientData, requireContext(), new RecyclerViewInterface() {
                                    @Override
                                    public void onClick(int position) {
                                        String receiverUID = patientData.get(position).getUserUID();
                                        String friendUserName = patientData.get(position).getFirstName() + " "
                                                + patientData.get(position).getSurname();
                                        String contact=patientData.get(position).getContact();
                                        String userType="doctor";

                                        chatIntent.putExtra("userName", friendUserName);
                                        chatIntent.putExtra("userUID", receiverUID);
                                        chatIntent.putExtra("Contact",contact);
                                        chatIntent.putExtra("userType",userType);

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
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        builder.create().show();
                                    }
                                });
                                patientAdapter.notifyDataSetChanged();
                                patientRecyclerView.setAdapter(patientAdapter);
                                loadingProgressLottie.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
            docRef=FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors");
            docRef.child(myUID).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);


                    if (doctor != null) {
                        profilePicUrl = doctor.getProfilePicUrl();
                        Glide.with(requireContext())
                                .load(profilePicUrl)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        doctorProfilePicImageView.setForeground(resource);
                                    }
                                });
                        sender = doctor.getFirstName() + " " + doctor.getSurname();
                    } else {
                        Toast.makeText(getContext(), "Please upload your profile info", Toast.LENGTH_SHORT).show();
                        docAccountTextIndicator.setText("Upload Info");
                        docAccountTextIndicator.setTextColor(Color.parseColor("#E30929"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch(Exception e){
            docAccountTextView.setTextColor(Color.parseColor("#E30929"));
            Toast.makeText(getContext(), "Please update your profile information", Toast.LENGTH_SHORT).show();
        }
    }
}