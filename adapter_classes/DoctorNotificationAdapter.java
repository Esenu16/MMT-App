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
import com.example.mmtapp.model_classes.BookingRequest;

import java.util.ArrayList;

public class DoctorNotificationAdapter extends RecyclerView.Adapter<DoctorNotificationAdapter.NewViewHolder> {
    ArrayList<BookingRequest> notifications;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public DoctorNotificationAdapter(ArrayList<BookingRequest> notifications, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.notifications = notifications;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.notification_design,parent,false);
        return new NewViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
       String senderName=notifications.get(position).getPatientName();
       holder.notificationSenderView.setText(senderName);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder{
        TextView notificationSenderView=itemView.findViewById(R.id.notification_sender_name);
        public NewViewHolder(@NonNull View itemView,@NonNull final RecyclerViewInterface recyclerViewInterface) {
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
    }
}
