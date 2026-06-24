package com.rishabh.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rishabh.chatapp.R;
import com.rishabh.chatapp.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<User> friends;
    private final OnFriendClickListener listener;

    public FriendsAdapter(
            Context context,
            ArrayList<User> friends,
            OnFriendClickListener listener
    ) {
        this.context = context;
        this.friends = friends;
        this.listener = listener;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
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

        User user = friends.get(position);

        holder.txtName.setText(
                user.username == null ? "User" : user.username
        );

        if ("online".equalsIgnoreCase(user.status)) {

            holder.txtStatus.setText("Online");
            holder.onlineDot.setVisibility(View.VISIBLE);

        } else {

            holder.txtStatus.setText("Offline");
            holder.onlineDot.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(user.profileImage)
                .placeholder(R.drawable.default_profile)
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(v ->
                listener.onChatClick(user));

        holder.btnVoiceCall.setOnClickListener(v ->
                listener.onVoiceCallClick(user));

        holder.btnVideoCall.setOnClickListener(v ->
                listener.onVideoCallClick(user));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public long getItemId(int position) {

        User user = friends.get(position);

        if (user.uid == null) {
            return position;
        }

        return user.uid.hashCode();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;

        View onlineDot;

        TextView txtName;
        TextView txtStatus;

        ImageView btnVoiceCall;
        ImageView btnVideoCall;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage =
                    itemView.findViewById(R.id.profileImage);

            onlineDot =
                    itemView.findViewById(R.id.onlineDot);

            txtName =
                    itemView.findViewById(R.id.txtName);

            txtStatus =
                    itemView.findViewById(R.id.txtStatus);

            btnVoiceCall =
                    itemView.findViewById(R.id.btnVoiceCall);

            btnVideoCall =
                    itemView.findViewById(R.id.btnVideoCall);
        }
    }

    public interface OnFriendClickListener {

        void onChatClick(User user);

        void onVoiceCallClick(User user);

        void onVideoCallClick(User user);
    }
}