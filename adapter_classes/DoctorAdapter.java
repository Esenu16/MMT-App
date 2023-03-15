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
import com.example.mmtapp.model_classes.Doctor;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.CustomViewHolder> {
    ArrayList<Doctor> doctorsData;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public DoctorAdapter(@NonNull ArrayList<Doctor> doctorsData, @NonNull Context context, @NonNull RecyclerViewInterface recyclerViewInterface) {
        this.doctorsData = doctorsData;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doctor_design, parent, false);
        return new CustomViewHolder(view, recyclerViewInterface);
    }

    //On bind view holder handles loading data on the template
    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        String doctorName = doctorsData.get(position).getFirstName() + " " + doctorsData.get(position).getSurname();
        String doctorAddress = doctorsData.get(position).getAddress();
        String profilePicUrl=doctorsData.get(position).getProfilePicUrl();

        holder.setData(doctorName, doctorAddress);
        Glide.with(holder.doctorImageView)
                .load(profilePicUrl)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        holder.doctorImageView.setForeground(resource);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return doctorsData.size();
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView doctorNameText = itemView.findViewById(R.id.doctor_name_text);
        TextView doctorAddressText = itemView.findViewById(R.id.doctor_address_textView);
        LinearLayout doctorImageView = itemView.findViewById(R.id.doctor_imageView);

        public CustomViewHolder(@NonNull View itemView, @NonNull final RecyclerViewInterface recyclerViewInterface) {
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

        public void setData(@NonNull String doctorName, @NonNull String doctorAddress) {
            doctorNameText.setText(doctorName);
            doctorAddressText.setText(doctorAddress);
        }
    }
}
