package com.rishabh.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewChatAdapter
        extends RecyclerView.Adapter<NewChatAdapter.ViewHolder> {

    Context context;
    ArrayList<User> users;

    public NewChatAdapter(
            Context context,
            ArrayList<User> users
    ) {

        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_new_chat_user,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        User user = users.get(position);

        holder.userName.setText(user.username);
        holder.userEmail.setText(user.email);

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            ChatActivity.class
                    );

            intent.putExtra("uid", user.uid);
            intent.putExtra("username", user.username);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView userName, userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName =
                    itemView.findViewById(R.id.userName);

            userEmail =
                    itemView.findViewById(R.id.userEmail);
        }
    }
}