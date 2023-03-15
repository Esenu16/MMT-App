package com.example.mmtapp.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmtapp.R;
import com.example.mmtapp.model_classes.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    ArrayList<Message> messages;
    Context context;
    private static final int CHAT_LEFT = 0;
    private static final int CHAT_RIGHT = 1;
    FirebaseUser fUser;

    public ChatAdapter(@NonNull ArrayList<Message> messages, @NonNull Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == CHAT_LEFT) {
            return new ChatViewHolder(inflater.inflate(R.layout.receive_design, parent, false));
        } else {
            return new ChatViewHolder(inflater.inflate(R.layout.send_design, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;

        if (messages.get(position).getSenderUID().equals(fUser.getUid())) {
            return CHAT_RIGHT;
        } else {
            return CHAT_LEFT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        String messageContent = messages.get(position).getContent();
        String time = messages.get(position).getTime();

        if (fUser.getUid().equals(messages.get(position).getSenderUID())) {
            holder.myContentView.setText(messageContent);
            holder.myTimeView.setText(time);
        } else {
            holder.friendContentView.setText(messageContent);
            holder.friendTimeView.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView myContentView = itemView.findViewById(R.id.my_content_textView);
        TextView myTimeView = itemView.findViewById(R.id.my_time_textView);
        TextView friendContentView = itemView.findViewById(R.id.friend_content_textView);
        TextView friendTimeView = itemView.findViewById(R.id.friend_time_textView);

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
