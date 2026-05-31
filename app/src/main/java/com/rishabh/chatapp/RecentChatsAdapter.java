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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecentChatsAdapter
        extends RecyclerView.Adapter<RecentChatsAdapter.ViewHolder> {

    Context context;
    ArrayList<User> users;

    public RecentChatsAdapter(
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
                                R.layout.user_card,
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

        // USERNAME

        holder.username.setText(user.username);

        // PROFILE IMAGE

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

        // CHAT ROOM

        String senderRoom =
                FirebaseAuth.getInstance()
                        .getUid()
                        + user.uid;

        // LAST MESSAGE

        FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(senderRoom)
                .limitToLast(1)
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                if (snapshot.exists()) {

                                    for (DataSnapshot data :
                                            snapshot.getChildren()) {

                                        Message message =
                                                data.getValue(
                                                        Message.class
                                                );

                                        if (message != null) {

                                            holder.lastMessage.setText(
                                                    message.message
                                            );

                                            long now =
                                                    System.currentTimeMillis();

                                            long diff =
                                                    now - message.timestamp;

                                            long sec =
                                                    diff / 1000;

                                            long min =
                                                    sec / 60;

                                            long hr =
                                                    min / 60;

                                            if (sec < 60) {

                                                holder.timeText.setText(
                                                        "now"
                                                );

                                            } else if (min < 60) {

                                                holder.timeText.setText(
                                                        min + " min"
                                                );

                                            } else {

                                                holder.timeText.setText(
                                                        hr + " hr"
                                                );
                                            }
                                        }
                                    }

                                } else {

                                    holder.lastMessage.setText(
                                            "Start chatting..."
                                    );

                                    holder.timeText.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        // OPEN CHAT

        holder.itemView.setOnClickListener(v -> {

            if (user.uid == null ||
                    user.uid.isEmpty()) {
                return;
            }

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
                    user.username
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

        TextView lastMessage;

        TextView timeText;

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

            lastMessage =
                    itemView.findViewById(
                            R.id.lastMessage
                    );

            timeText =
                    itemView.findViewById(
                            R.id.timeText
                    );
        }
    }
}