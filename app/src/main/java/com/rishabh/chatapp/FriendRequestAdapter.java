package com.rishabh.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendRequestAdapter
        extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    Context context;

    ArrayList<User> users;

    public FriendRequestAdapter(
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
                                R.layout.request_card,
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

        // =========================
        // USERNAME FIX
        // =========================

        String name = "User";

        if (user.username != null &&
                !user.username.trim().isEmpty()) {

            name = user.username;

        } else if (user.email != null &&
                !user.email.trim().isEmpty()) {

            name = user.email.split("@")[0];
        }

        holder.username.setText(name);

        // =========================
        // REQUEST TEXT
        // =========================

        holder.timeText.setText(
                "Sends request"
        );

        // =========================
        // ACCEPT BUTTON
        // =========================

        holder.acceptBtn.setOnClickListener(v -> {

            int adapterPosition =
                    holder.getAdapterPosition();

            if (adapterPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }

            String currentUid =
                    FirebaseAuth.getInstance()
                            .getUid();

            if (currentUid == null
                    || user.uid == null) {

                Toast.makeText(
                        context,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ADD FRIEND BOTH SIDES

            FirebaseDatabase.getInstance()
                    .getReference("Friends")
                    .child(currentUid)
                    .child(user.uid)
                    .setValue(true);

            FirebaseDatabase.getInstance()
                    .getReference("Friends")
                    .child(user.uid)
                    .child(currentUid)
                    .setValue(true);

            // REMOVE REQUEST

            FirebaseDatabase.getInstance()
                    .getReference("FriendRequests")
                    .child(currentUid)
                    .child(user.uid)
                    .removeValue();

            Toast.makeText(
                    context,
                    "Friend Added",
                    Toast.LENGTH_SHORT
            ).show();

            users.remove(adapterPosition);

            notifyItemRemoved(adapterPosition);

            notifyItemRangeChanged(
                    adapterPosition,
                    users.size()
            );
        });

        // =========================
        // REJECT BUTTON
        // =========================

        holder.rejectBtn.setOnClickListener(v -> {

            int adapterPosition =
                    holder.getAdapterPosition();

            if (adapterPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }

            String currentUid =
                    FirebaseAuth.getInstance()
                            .getUid();

            if (currentUid == null
                    || user.uid == null) {

                return;
            }

            FirebaseDatabase.getInstance()
                    .getReference("FriendRequests")
                    .child(currentUid)
                    .child(user.uid)
                    .removeValue();

            Toast.makeText(
                    context,
                    "Request Rejected",
                    Toast.LENGTH_SHORT
            ).show();

            users.remove(adapterPosition);

            notifyItemRemoved(adapterPosition);

            notifyItemRangeChanged(
                    adapterPosition,
                    users.size()
            );
        });
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    // =========================
    // VIEW HOLDER
    // =========================

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView profileImage;

        TextView username;

        TextView timeText;

        Button acceptBtn;

        Button rejectBtn;

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

            timeText =
                    itemView.findViewById(
                            R.id.timeText
                    );

            acceptBtn =
                    itemView.findViewById(
                            R.id.acceptBtn
                    );

            rejectBtn =
                    itemView.findViewById(
                            R.id.rejectBtn
                    );
        }
    }
}