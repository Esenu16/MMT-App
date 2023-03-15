package com.example.mmtapp.patient_classes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.adapter_classes.PatientNotificationAdapter;
import com.example.mmtapp.model_classes.BookingResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationFragment extends Fragment {
    RecyclerView recyclerView;
    PatientNotificationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<BookingResponse>responseData;
    BookingResponse response;
    DatabaseReference responseRef;
    String myUID;
    TextView patientNotificationIndicator;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView=view.findViewById(R.id.patientsNotificationRecyclerView);
        linearLayoutManager=new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        patientNotificationIndicator=view.findViewById(R.id.patient_textIndicator);
        patientNotificationIndicator.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);

        myUID= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try{
            responseRef= FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses");
        } catch(DatabaseException e){
            e.printStackTrace();
        }

        responseData=new ArrayList<>();
        fetchBookingResponses();
        adapter=new PatientNotificationAdapter(responseData,requireContext(), new RecyclerViewInterface() {
            @Override
            public void onClick(int position) {
                FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses").child(myUID).removeValue();
                Toast.makeText(requireContext(), "Check your appointments for details", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void fetchBookingResponses() {
        responseRef.child(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    response=dataSnapshot.getValue(BookingResponse.class);
                    if(response!=null) {
                        if (response.getPatientUID().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            responseData.add(response);
                            adapter.notifyDataSetChanged();
                            patientNotificationIndicator.setVisibility(View.GONE);
                            recyclerView.setAdapter(adapter);
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