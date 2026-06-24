package com.rishabh.chatapp.adapters;

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
import com.rishabh.chatapp.ChatActivity;
import com.rishabh.chatapp.R;
import com.rishabh.chatapp.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter
        extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<User> chatList;

    public ChatAdapter(
            Context context,
            ArrayList<User> chatList) {

        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_chat,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        User user = chatList.get(position);

        holder.txtName.setText(
                user.getFullName()
        );

        holder.txtMessage.setText(
                user.lastMessage
        );

        if (user.lastMessageTime > 0) {

            holder.txtTime.setText(
                    new SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                    ).format(
                            new Date(
                                    user.lastMessageTime
                            )
                    )
            );
        } else {
            holder.txtTime.setText("");
        }

        Glide.with(context)
                .load(user.profileImage)
                .placeholder(
                        R.drawable.default_profile
                )
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {

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
                    user.getFullName()
            );

            intent.putExtra(
                    "profileImage",
                    user.profileImage
            );

            context.startActivity(intent);
        });

        if (user.unreadCount > 0) {

            holder.unreadBadge.setVisibility(
                    View.VISIBLE
            );

            holder.unreadBadge.setText(
                    String.valueOf(user.unreadCount)
            );

        } else {

            holder.unreadBadge.setVisibility(
                    View.GONE
            );
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView profileImage;

        TextView txtName;
        TextView txtMessage;
        TextView txtTime;

        TextView unreadBadge;

        ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            profileImage =
                    itemView.findViewById(
                            R.id.profileImage
                    );

            txtName = itemView.findViewById(R.id.userName);
            txtMessage = itemView.findViewById(R.id.lastMessage);
            txtTime = itemView.findViewById(R.id.timeText);
            unreadBadge =
                    itemView.findViewById(
                            R.id.unreadBadge
                    );
        }
    }
}