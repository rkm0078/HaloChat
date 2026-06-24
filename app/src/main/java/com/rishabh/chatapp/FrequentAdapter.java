package com.rishabh.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
                                R.layout.item_chat,
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

        String name = user.getFullName();

        if (name == null || name.trim().isEmpty()) {
            name = user.username;
        }

        if (name == null || name.trim().isEmpty()) {
            name = "User";
        }

        holder.userName.setText(name);

        if (user.lastMessage != null) {

            holder.lastMessage.setText(
                    user.lastMessage
            );

        } else {

            holder.lastMessage.setText("");
        }

        if (user.lastMessageTime > 0) {

            holder.timeText.setText(
                    new SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                    ).format(
                            new Date(user.lastMessageTime)
                    )
            );

        } else {

            holder.timeText.setText("");
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

        // UNREAD COUNT

        System.out.println(
                "ADAPTER -> "
                        + user.getFullName()
                        + " unread = "
                        + user.unreadCount
        );

        if (user.unreadCount > 0) {

            holder.unreadCount.setVisibility(
                    user.unreadCount > 0 ? View.VISIBLE : View.GONE
            );

        } else {

            holder.unreadCount.setVisibility(
                    View.GONE
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
                    user.getFullName()
            );

            context.startActivity(intent);
        });

        holder.itemView.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    v.animate()
                            .scaleX(0.97f)
                            .scaleY(0.97f)
                            .setDuration(100)
                            .start();

                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_CANCEL:

                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                    break;
            }

            return false;
        });
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView profileImage;

        TextView userName;
        TextView lastMessage;
        TextView timeText;
        TextView unreadCount;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            profileImage =
                    itemView.findViewById(
                            R.id.profileImage
                    );

            userName =
                    itemView.findViewById(
                            R.id.userName
                    );

            lastMessage =
                    itemView.findViewById(
                            R.id.lastMessage
                    );

            timeText =
                    itemView.findViewById(
                            R.id.timeText
                    );

            unreadCount =
                    itemView.findViewById(
                            R.id.unreadCount
                    );
        }
    }
}