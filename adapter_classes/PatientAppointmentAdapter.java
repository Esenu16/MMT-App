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

public class PatientAppointmentAdapter extends RecyclerView.Adapter<PatientAppointmentAdapter.AppointmentsViewHolder> {
    ArrayList<BookingResponse>appointmentsData;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public PatientAppointmentAdapter(ArrayList<BookingResponse> appointmentsData, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.appointmentsData = appointmentsData;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.appointment_details,parent,false);
        return new AppointmentsViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position) {
        String doctorName="Doctor name: "+appointmentsData.get(position).getDoctorName();
        String date="Replied on: "+appointmentsData.get(position).getResponseDate();
        holder.setData(doctorName,date);
    }

    @Override
    public int getItemCount() {
        return appointmentsData.size();
    }

    public static class AppointmentsViewHolder extends RecyclerView.ViewHolder{
        TextView doctorNameView=itemView.findViewById(R.id.doctor_appointment_name);
        TextView dateView=itemView.findViewById(R.id.appointment_date_textView);
        LinearLayout imageLinearLayout=itemView.findViewById(R.id.contacts_patient_imageView);

        public AppointmentsViewHolder(@NonNull View itemView,@NonNull final RecyclerViewInterface recyclerViewInterface) {
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

       public void setData(String doctorName, String date) {
            doctorNameView.setText(doctorName);
            dateView.setText(date);
        }
    }
}
