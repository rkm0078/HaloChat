package com.rishabh.chatapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerAdapter
        extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    private final Context context;

    private final ArrayList<User> users;

    private final HashSet<String> friendIds;

    private final HashSet<String> sentRequestIds;

    private final HashSet<String> receivedRequestIds;

    private final OnFriendClickListener listener;

    public UserRecyclerAdapter(
            Context context,
            ArrayList<User> users,
            HashSet<String> friendIds,
            HashSet<String> sentRequestIds,
            HashSet<String> receivedRequestIds,
            OnFriendClickListener listener
    ) {

        this.context = context;
        this.users = users;
        this.friendIds = friendIds;
        this.sentRequestIds = sentRequestIds;
        this.receivedRequestIds = receivedRequestIds;
        this.listener = listener;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.user_suggestion_card,
                                parent,
                                false
                        );

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position
    ) {

        User user = users.get(position);

        // Username

        holder.username.setText(
                user.username == null || user.username.isEmpty()
                        ? "User"
                        : user.username
        );

        // Mutual text default

        holder.mutualText.setText(
                "People you may know"
        );

        // Profile Image

        Glide.with(context)
                .load(user.profileImage)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(holder.profileImage);

        // Open Profile

        holder.profileImage.setOnClickListener(v -> {

            Toast.makeText(
                    context,
                    "Profile feature coming soon",
                    Toast.LENGTH_SHORT
            ).show();

        });

        holder.username.setOnClickListener(v -> {

            Toast.makeText(
                    context,
                    "Profile feature coming soon",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================
        // BUTTON STATE
        // =========================

        holder.addFriendBtn.setEnabled(true);
        holder.addFriendBtn.setAlpha(1f);

        if (friendIds.contains(user.uid)) {

            holder.mutualText.setText("Connected");

            holder.addFriendBtn.setText("Friends");

            holder.addFriendBtn.setEnabled(false);

            holder.addFriendBtn.setAlpha(0.7f);

            holder.addFriendBtn.setStrokeColor(
                    ColorStateList.valueOf(
                            Color.parseColor("#555555")
                    )
            );

            return;
        }


        if (sentRequestIds.contains(user.uid)) {

            holder.mutualText.setText("Request Sent");

            holder.addFriendBtn.setEnabled(false);
            holder.addFriendBtn.setAlpha(0.5f);

            return;
        }

        if (receivedRequestIds.contains(user.uid)) {

            holder.mutualText.setText("Request Received");

            holder.addFriendBtn.setEnabled(false);
            holder.addFriendBtn.setAlpha(0.5f);

            return;
        }

        // =========================
        // SEND FRIEND REQUEST
        // =========================

        holder.addFriendBtn.setOnClickListener(v -> {

            String currentUid =
                    FirebaseAuth.getInstance().getUid();

            if (currentUid == null) {
                return;
            }

            HashMap<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "/FriendRequests/"
                            + currentUid
                            + "/sent/"
                            + user.uid,
                    true
            );

            updates.put(
                    "/FriendRequests/"
                            + user.uid
                            + "/received/"
                            + currentUid,
                    true
            );

            FirebaseDatabase.getInstance()
                    .getReference()
                    .updateChildren(updates)
                    .addOnSuccessListener(unused -> {

                        sentRequestIds.add(user.uid);

                        holder.mutualText.setText(
                                "Request Sent"
                        );

                        holder.addFriendBtn.setEnabled(false);
                        holder.addFriendBtn.setAlpha(0.5f);

                        Toast.makeText(
                                context,
                                "Friend request sent",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

    }

    // =========================
    // VIEW HOLDER
    // =========================

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        final CircleImageView profileImage;

        final TextView username;

        final TextView mutualText;

        final MaterialButton addFriendBtn;

        public UserViewHolder(
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

            mutualText =
                    itemView.findViewById(
                            R.id.mutualText
                    );

            addFriendBtn =
                    itemView.findViewById(
                            R.id.addFriendBtn
                    );
        }
    }

    public interface OnFriendClickListener {

        void onChatClick(User user);

        void onVoiceCallClick(User user);

        void onVideoCallClick(User user);
    }

    @Override
    public int getItemCount () {

        return users.size();
    }

    @Override
    public long getItemId ( int position){

        User user = users.get(position);

        if (user.uid == null) {
            return position;
        }

        return user.uid.hashCode();
    }
}