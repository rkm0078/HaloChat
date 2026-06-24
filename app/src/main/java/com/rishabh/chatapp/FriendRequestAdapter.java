package com.rishabh.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter
        extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private final Context context;

    private final ArrayList<User> users;

    public FriendRequestAdapter(
            Context context,
            ArrayList<User> users
    ) {

        this.context = context;
        this.users = users;

        setHasStableIds(true);
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
                                R.layout.item_friend_request,
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

        if (position < 0 || position >= users.size()) {
            return;
        }

        User user = users.get(position);

        if (user == null) {
            return;
        }

        // Username

        holder.username.setText(
                user.username == null || user.username.isEmpty()
                        ? "User"
                        : user.username
        );

        // Full Name

        if (holder.fullName != null) {
            holder.fullName.setText(
                    user.getFullName() == null
                            ? ""
                            : user.getFullName()
            );
        }

        // Profile Image

        Glide.with(context)
                .load(
                        user.profileImage == null
                                ? ""
                                : user.profileImage
                )
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(holder.profileImage);

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

        // Accept Button

        holder.acceptBtn.setOnClickListener(v -> {

            String currentUid =
                    FirebaseAuth.getInstance().getUid();

            if (currentUid == null) {
                return;
            }

            if (user.uid == null || user.uid.isEmpty()) {
                return;
            }

            HashMap<String, Object> updates =
                    new HashMap<>();

            // =========================
            // ADD TO FRIENDS
            // =========================

            updates.put(
                    "/Friends/"
                            + currentUid
                            + "/"
                            + user.uid,
                    true
            );

            updates.put(
                    "/Friends/"
                            + user.uid
                            + "/"
                            + currentUid,
                    true
            );

            // =========================
            // REMOVE FRIEND REQUEST
            // =========================

            updates.put(
                    "/FriendRequests/"
                            + currentUid
                            + "/received/"
                            + user.uid,
                    null
            );

            updates.put(
                    "/FriendRequests/"
                            + user.uid
                            + "/sent/"
                            + currentUid,
                    null
            );

            // Prevent double click

            holder.acceptBtn.setEnabled(false);
            holder.rejectBtn.setEnabled(false);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .updateChildren(updates)
                    .addOnSuccessListener(unused -> {

                        int adapterPosition =
                                holder.getBindingAdapterPosition();

                        if (adapterPosition != RecyclerView.NO_POSITION) {

                            users.remove(adapterPosition);

                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, users.size());
                        }

                        Toast.makeText(
                                context,
                                "You are now friends!",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {

                        holder.acceptBtn.setEnabled(true);
                        holder.rejectBtn.setEnabled(true);

                        Toast.makeText(
                                context,
                                "Operation failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        // =========================
        // REJECT REQUEST
        // =========================

        holder.rejectBtn.setOnClickListener(v -> {

            String currentUid =
                    FirebaseAuth.getInstance().getUid();

            if (currentUid == null) {
                return;
            }

            if (user.uid == null || user.uid.isEmpty()) {
                return;
            }

            HashMap<String, Object> updates =
                    new HashMap<>();

            // Remove request from receiver

            updates.put(
                    "/FriendRequests/"
                            + currentUid
                            + "/received/"
                            + user.uid,
                    null
            );

            // Remove request from sender

            updates.put(
                    "/FriendRequests/"
                            + user.uid
                            + "/sent/"
                            + currentUid,
                    null
            );

            // Prevent double click

            holder.acceptBtn.setEnabled(false);
            holder.rejectBtn.setEnabled(false);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .updateChildren(updates)
                    .addOnSuccessListener(unused -> {

                        int adapterPosition =
                                holder.getBindingAdapterPosition();

                        if (adapterPosition
                                != RecyclerView.NO_POSITION) {

                            users.remove(adapterPosition);

                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, users.size());
                        }

                        Toast.makeText(
                                context,
                                "Request declined",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {

                        holder.acceptBtn.setEnabled(true);
                        holder.rejectBtn.setEnabled(true);

                        Toast.makeText(
                                context,
                                "Operation failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

        });
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public long getItemId(int position) {

        User user = users.get(position);

        if (user == null || user.uid == null) {
            return position;
        }

        return user.uid.hashCode();
    }

    // =========================
    // VIEW HOLDER
    // =========================

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        final CircleImageView profileImage;

        final TextView username;

        final TextView fullName;

        final Button acceptBtn;

        final Button rejectBtn;

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

            fullName =
                    itemView.findViewById(R.id.requestText);

            acceptBtn =
                    itemView.findViewById(
                            R.id.acceptBtn
                    );

            rejectBtn =
                    itemView.findViewById(R.id.declineBtn);
        }
    }
}
