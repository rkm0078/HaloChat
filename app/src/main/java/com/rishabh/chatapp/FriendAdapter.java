package com.rishabh.chatapp;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter
        extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private final ArrayList<User> friends;

    public FriendAdapter(ArrayList<User> friends) {

        this.friends = friends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_friend,
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

        User user =
                friends.get(position);

        holder.fullName.setText(
                user.getFullName()
        );

        holder.username.setText(
                "@" + user.getSafeUsername()
        );

        if (user.profileImage != null &&
                !user.profileImage.equals("default")) {

            Glide.with(holder.itemView.getContext())
                    .load(user.profileImage)
                    .into(holder.profileImage);

        } else {

            holder.profileImage.setImageResource(
                    R.drawable.default_profile
            );
        }

        holder.messageBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            holder.itemView.getContext(),
                            ChatActivity.class
                    );

            intent.putExtra(
                    "uid",
                    user.uid
            );

            intent.putExtra(
                    "username",
                    user.getFullName()
            );

            holder.itemView
                    .getContext()
                    .startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return friends.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView profileImage;

        TextView fullName,
                username;

        ImageView messageBtn,
                callBtn;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            profileImage =
                    itemView.findViewById(
                            R.id.profileImage
                    );

            fullName =
                    itemView.findViewById(
                            R.id.fullName
                    );

            username =
                    itemView.findViewById(
                            R.id.username
                    );

            messageBtn =
                    itemView.findViewById(
                            R.id.messageBtn
                    );

            callBtn =
                    itemView.findViewById(
                            R.id.callBtn
                    );
        }
    }
}