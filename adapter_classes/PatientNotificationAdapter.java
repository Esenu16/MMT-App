package com.example.mmtapp.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mmtapp.R;
import com.example.mmtapp.UI_classes.RecyclerViewInterface;
import com.example.mmtapp.model_classes.BookingResponse;

import java.util.ArrayList;

public class PatientNotificationAdapter extends RecyclerView.Adapter<PatientNotificationAdapter.NotificationViewHolder> {
    ArrayList<BookingResponse>bookingResponses;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public PatientNotificationAdapter(@NonNull ArrayList<BookingResponse> bookingResponses, @NonNull Context context, @NonNull RecyclerViewInterface recyclerViewInterface) {
        this.bookingResponses = bookingResponses;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.response_design,parent,false);
        return new NotificationViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        String doctorName=bookingResponses.get(position).getDoctorName();
        holder.setData(doctorName);
    }

    @Override
    public int getItemCount() {
        return bookingResponses.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{
        TextView senderName=itemView.findViewById(R.id.doctor_sender_name);

        public NotificationViewHolder(@NonNull View itemView, @NonNull final RecyclerViewInterface recyclerViewInterface) {
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

        public void setData(String doctorName) {
            senderName.setText(doctorName);
        }
    }
}
