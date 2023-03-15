package com.example.mmtapp.doctor_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.ChatScreen;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.adapter_classes.ContactsAdapter;
import com.example.mmtapp.model_classes.BookingResponse;
import com.example.mmtapp.model_classes.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorsAppointmentsFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ContactsAdapter contactsAdapter;
    ArrayList<BookingResponse> responseData;
    DatabaseReference responseRef;
    Intent chatIntent;
    String telContact,myUID;

    public DoctorsAppointmentsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_doctors_appointments, container, false);
        recyclerView=view.findViewById(R.id.contacts_recyclerView);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        myUID= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try{
            responseRef= FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Contacts");
        }catch(DatabaseException e){
            e.printStackTrace();
        }
        responseData=new ArrayList<>();

        chatIntent=new Intent(getContext(),ChatScreen.class);
        contactsAdapter=new ContactsAdapter(responseData, requireContext(), new RecyclerViewInterface() {
            @Override
            public void onClick(int position) {
                String friendUserName=responseData.get(position).getPatientName();
                String receiverUID=responseData.get(position).getPatientUID();
                String userType="doctor";
                chatIntent.putExtra("userName",friendUserName);
                chatIntent.putExtra("userUID",receiverUID);
                chatIntent.putExtra("userType",userType);
                chatIntent.putExtra("Contact",telContact);
                startActivity(chatIntent);
            }
        });
        fetchResponses();
        retrieveMyData();
        return view;
       }

    private void fetchResponses() {
        responseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    BookingResponse response = snapshot.getValue(BookingResponse.class);
                    assert response != null;
                    if (response.getDoctorUID().equals(myUID)) {
                        telContact = response.getPatientContact();
                        responseData.add(response);
                        contactsAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(contactsAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void retrieveMyData(){
        try{
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Doctor doctor=dataSnapshot.getValue(Doctor.class);
                    assert doctor != null;
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