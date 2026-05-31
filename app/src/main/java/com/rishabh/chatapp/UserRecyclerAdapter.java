package com.rishabh.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UserRecyclerAdapter
        extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    Context context;

    ArrayList<User> users;

    HashSet<String> friendIds;

    HashSet<String> requestIds;

    public UserRecyclerAdapter(
            Context context,
            ArrayList<User> users,
            HashSet<String> friendIds,
            HashSet<String> requestIds
    ) {

        this.context = context;

        this.users = users;

        this.friendIds = friendIds;

        this.requestIds = requestIds;
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

        // =========================
        // USERNAME
        // =========================

        if (user.username != null &&
                !user.username.isEmpty()) {

            holder.username.setText(
                    user.username
            );

        } else {

            holder.username.setText(
                    "User"
            );
        }

        // =========================
        // PROFILE IMAGE
        // =========================

        Glide.with(context)
                .load(user.profileImage)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(holder.profileImage);

        holder.mutualText.setText(
                "People you may know"
        );

        // =========================
        // OPEN PROFILE
        // =========================

        holder.itemView.setOnClickListener(v -> {

            if (user.uid == null)
                return;

            Intent intent =
                    new Intent(
                            context,
                            FriendProfileActivity.class
                    );

            intent.putExtra(
                    "uid",
                    user.uid
            );

            context.startActivity(intent);
        });

        // =========================
        // ALREADY FRIEND
        // =========================

        if (friendIds.contains(user.uid)) {

            holder.mutualText.setText(
                    "Already Friend"
            );

            holder.addFriendBtn.setAlpha(0.4f);

            holder.addFriendBtn.setEnabled(false);

            return;
        }

        // =========================
        // REQUEST ALREADY SENT
        // =========================

        if (requestIds.contains(user.uid)) {

            holder.mutualText.setText(
                    "Request Sent"
            );

            holder.addFriendBtn.setAlpha(0.4f);

            holder.addFriendBtn.setEnabled(false);

            return;
        }

        // =========================
        // NORMAL STATE
        // =========================

        holder.addFriendBtn.setAlpha(1f);

        holder.addFriendBtn.setEnabled(true);

        // =========================
        // SEND FRIEND REQUEST
        // =========================

        holder.addFriendBtn.setOnClickListener(v -> {

            String currentUid =
                    FirebaseAuth.getInstance()
                            .getUid();

            if (currentUid == null)
                return;

            DatabaseReference requestRef =
                    FirebaseDatabase.getInstance()
                            .getReference("FriendRequests")
                            .child(user.uid);

            HashMap<String, Object> map =
                    new HashMap<>();

            map.put("uid", currentUid);

            map.put(
                    "time",
                    System.currentTimeMillis()
            );

            requestRef.child(currentUid)
                    .setValue(map)

                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                context,
                                "Friend Request Sent",
                                Toast.LENGTH_SHORT
                        ).show();

                        holder.mutualText.setText(
                                "Request Sent"
                        );

                        holder.addFriendBtn.setAlpha(0.4f);

                        holder.addFriendBtn.setEnabled(false);

                        requestIds.add(user.uid);
                    });
        });
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    // =========================
    // VIEW HOLDER
    // =========================

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView profileImage;

        TextView username;

        TextView mutualText;

        LinearLayout addFriendBtn;

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
}