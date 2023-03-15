package com.example.mmtapp.patient_classes;

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
import com.example.mmtapp.adapter_classes.PatientAppointmentAdapter;
import com.example.mmtapp.model_classes.BookingResponse;
import com.example.mmtapp.model_classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AppointmentFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    PatientAppointmentAdapter adapter;
    ArrayList<BookingResponse> appointmentsData;
    DatabaseReference ref;
    Intent docChatIntent;
    String myUID;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_appointment, container, false);
        recyclerView=view.findViewById(R.id.patient_appointments_recyclerView);
        layoutManager=new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        myUID= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



        try{
            ref= FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Contacts");
        }catch(DatabaseException e){
            e.printStackTrace();
        }

        appointmentsData=new ArrayList<>();
        adapter=new PatientAppointmentAdapter(appointmentsData, requireContext(), new RecyclerViewInterface() {
            @Override
            public void onClick(int position) {
                String userUID=appointmentsData.get(position).getDoctorUID();
                String doctorName=appointmentsData.get(position).getDoctorName();
                String doctorContact=appointmentsData.get(position).getDoctorContact();

                String userType="patient";

                docChatIntent=new Intent(getContext(), ChatScreen.class);
                docChatIntent.putExtra("userUID",userUID);
                docChatIntent.putExtra("userName",doctorName);
                docChatIntent.putExtra("userType",userType);
                docChatIntent.putExtra("docContact",doctorContact);
                FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Responses").child(myUID).removeValue();
                startActivity(docChatIntent);
            }
        });
        fetchAppointments();
        retrieveMyData();
        return view;
    }

    private void fetchAppointments() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    BookingResponse response=snapshot.getValue(BookingResponse.class);
                    appointmentsData.add(response);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void retrieveMyData(){
        try{
            FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users").
                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user=dataSnapshot.getValue(User.class);
                            assert user != null;
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