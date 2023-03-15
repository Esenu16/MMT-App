package com.example.mmtapp.adapter_classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.model_classes.BookingResponse;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    ArrayList<BookingResponse> contactsData;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public ContactsAdapter(ArrayList<BookingResponse> contactsData, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.contactsData = contactsData;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.contacts_design,parent,false);
        return new ContactsViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, final int position) {

       String date="Contacted on: "+contactsData.get(position).getResponseDate();
       String time="Time for appointment :"+contactsData.get(position).getResponseTime();
       String name=contactsData.get(position).getPatientName();
       String profilePicUrl=contactsData.get(position).getPatientProfilePicUrl();
       holder.setData(name,date,time,profilePicUrl);
    }

    @Override
    public int getItemCount() {
        return contactsData.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout patientsImageView=itemView.findViewById(R.id.contacts_patient_imageView);
        TextView nameView=itemView.findViewById(R.id.contacts_patients_appointments_name_view);
        TextView dateView=itemView.findViewById(R.id.contacts_appointments_date_view);
        TextView timeView=itemView.findViewById(R.id.contacts_appointments_time_view);
        public ContactsViewHolder(@NonNull View itemView, @NonNull final RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        try {
                            recyclerViewInterface.onClick(pos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public void setData(String name, String date, String time, String profilePicUrl) {
            nameView.setText(name);
            timeView.setText(time);
            dateView.setText(date);
            Glide.with(patientsImageView)
                    .load(profilePicUrl)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                           patientsImageView.setForeground(resource);
                        }
                    });
        }
    }
}
