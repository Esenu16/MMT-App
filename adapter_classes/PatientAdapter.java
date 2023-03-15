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
import com.example.mmtapp.model_classes.User;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {
    ArrayList<User> patientData;
    Context context;
    RecyclerViewInterface recyclerViewInterface;


    public PatientAdapter(@NonNull ArrayList<User> patientData, @NonNull Context context, @NonNull RecyclerViewInterface recyclerViewInterface) {
        this.patientData = patientData;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.patients_design, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        String patientName = patientData.get(position).getFirstName() + " " + patientData.get(position).getSurname();
        String profilePicUrl=patientData.get(position).getProfilePicUrl();
        holder.setData(patientName, profilePicUrl);
    }

    @Override
    public int getItemCount() {
        return patientData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView patientNameText = itemView.findViewById(R.id.patient_name_text);
        LinearLayout patientImageView = itemView.findViewById(R.id.patient_imageView);

        public MyViewHolder(@NonNull View itemView, @NonNull final RecyclerViewInterface recyclerViewInterface) {
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

        public void setData(@NonNull String patientName, @NonNull String profilePicUrl) {
            patientNameText.setText(patientName);
            Glide.with(patientImageView)
                    .load(profilePicUrl)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            patientImageView.setForeground(resource);
                        }
                    });
        }
    }
}
