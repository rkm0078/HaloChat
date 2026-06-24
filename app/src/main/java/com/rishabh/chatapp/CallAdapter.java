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
import com.rishabh.chatapp.models.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Call> callList;

    public CallAdapter(Context context, ArrayList<Call> callList) {
        this.context = context;
        this.callList = callList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_call, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Call call = callList.get(position);

        holder.userName.setText(call.userName);

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd MMM • hh:mm a",
                        Locale.getDefault());

        holder.callTime.setText(
                sdf.format(new Date(call.timestamp))
        );

        Glide.with(context)
                .load(call.profileImage)
                .placeholder(R.drawable.default_profile)
                .into(holder.profileImage);

        if (call.isVideo) {
            holder.callTypeIcon.setImageResource(
                    R.drawable.ic_video_call);
        } else {
            holder.callTypeIcon.setImageResource(
                    R.drawable.ic_call);
        }

        if (call.isMissed) {

            holder.callStatusIcon.setImageResource(
                    R.drawable.ic_call_missed);

        } else if (call.isIncoming) {

            holder.callStatusIcon.setImageResource(
                    R.drawable.ic_call_received);

        } else {

            holder.callStatusIcon.setImageResource(
                    R.drawable.ic_call_made);
        }
    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView userName;
        TextView callTime;
        ImageView callStatusIcon;
        ImageView callTypeIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage =
                    itemView.findViewById(R.id.profileImage);

            userName =
                    itemView.findViewById(R.id.userName);

            callTime =
                    itemView.findViewById(R.id.callTime);

            callStatusIcon =
                    itemView.findViewById(R.id.callStatusIcon);

            callTypeIcon =
                    itemView.findViewById(R.id.callTypeIcon);
        }
    }
}