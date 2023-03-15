package com.example.mmtapp.doctor_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.BookingScreen;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.adapter_classes.DoctorNotificationAdapter;
import com.example.mmtapp.model_classes.BookingRequest;
import com.example.mmtapp.model_classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorsNotificationsFragment extends Fragment {
    RecyclerView doctorsNotificationRecyclerView;
    DoctorNotificationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<BookingRequest> notifications;
    DatabaseReference bookingRef;
    BookingRequest request;
    String myUID,patientUID,patientName;
    TextView doctorNotificationIndicator;
    Intent responseIntent;
    public DoctorsNotificationsFragment() {
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
        View v=inflater.inflate(R.layout.fragment_doctors_notifications, container, false);
        doctorsNotificationRecyclerView=v.findViewById(R.id.doctor_notification_recyclerView);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        doctorsNotificationRecyclerView.setLayoutManager(linearLayoutManager);
        doctorsNotificationRecyclerView.setHasFixedSize(true);
        doctorNotificationIndicator=v.findViewById(R.id.doctor_textIndicator);
        doctorNotificationIndicator.setVisibility(View.VISIBLE);

        myUID=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try{
            bookingRef= FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Requests");
        }catch(DatabaseException e){
            e.printStackTrace();
        }

        notifications=new ArrayList<>();

        adapter=new DoctorNotificationAdapter(notifications, requireContext(), new RecyclerViewInterface() {
            @Override
            public void onClick(int position) {
                patientUID=notifications.get(position).getPatientUID();
                patientName=notifications.get(position).getPatientName();
                String patientContact=notifications.get(position).getPatientContact();
                responseIntent=new Intent(requireContext(), BookingScreen.class);
                responseIntent.putExtra("patientUID",patientUID);
                responseIntent.putExtra("patientName",patientName);
                responseIntent.putExtra("Contact",patientContact);

                bookingRef.child(myUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(responseIntent);
                    }
                });
            }
        });
        fetchBookingRequests();
        return v;
    }

    private void fetchBookingRequests() {
        bookingRef.child(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    request=dataSnapshot.getValue(BookingRequest.class);

                    if(request!=null) {
                        if (request.getDoctorUID().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            notifications.add(request);
                            adapter.notifyDataSetChanged();
                            doctorNotificationIndicator.setVisibility(View.GONE);
                            doctorsNotificationRecyclerView.setAdapter(adapter);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}