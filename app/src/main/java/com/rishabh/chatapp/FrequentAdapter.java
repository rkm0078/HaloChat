package com.rishabh.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FrequentAdapter
        extends RecyclerView.Adapter<FrequentAdapter.ViewHolder> {

    Context context;

    ArrayList<User> users;

    public FrequentAdapter(
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
                                R.layout.item_frequent_user,
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

        // SAFE USERNAME

        if (user.username != null &&
                !user.username.isEmpty()) {

            holder.username.setText(user.username);

        } else {

            holder.username.setText("User");
        }

        // SAFE IMAGE

        if (user.profileImage != null &&
                !user.profileImage.isEmpty() &&
                !user.profileImage.equals("default")) {

            Glide.with(context)
                    .load(user.profileImage)
                    .into(holder.profileImage);

        } else {

            holder.profileImage.setImageResource(
                    R.drawable.default_profile
            );
        }

        // OPEN CHAT

        holder.itemView.setOnClickListener(v -> {

            // IMPORTANT SAFETY CHECK

            if (user == null)
                return;

            if (user.uid == null)
                return;

            if (user.uid.isEmpty())
                return;

            Intent intent =
                    new Intent(
                            context,
                            ChatActivity.class
                    );

            intent.putExtra(
                    "uid",
                    user.uid
            );

            intent.putExtra(
                    "username",
                    user.username == null
                            ? "User"
                            : user.username
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView profileImage;

        TextView username;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            profileImage =
                    itemView.findViewById(
                            R.id.profileImage
                    );

            username =
                    itemView.findViewById(
                            R.id.username
                    );
        }
    }
}