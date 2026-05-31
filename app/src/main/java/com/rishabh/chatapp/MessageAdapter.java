package com.rishabh.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    ArrayList<Message> messages;

    String receiverUid;

    int ITEM_SENT = 1;
    int ITEM_RECEIVE = 2;

    public MessageAdapter(
            Context context,
            ArrayList<Message> messages,
            String receiverUid
    ) {

        this.context = context;
        this.messages = messages;
        this.receiverUid = receiverUid;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);

        if (message.senderId.equals(receiverUid)) {

            return ITEM_RECEIVE;

        } else {

            return ITEM_SENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == ITEM_SENT) {

            View view =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.item_sender,
                                    parent,
                                    false
                            );

            return new SenderViewHolder(view);

        } else {

            View view =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.item_receiver,
                                    parent,
                                    false
                            );

            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {

        Message message = messages.get(position);

        String time =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                ).format(
                        new Date(message.timestamp)
                );

        if (holder instanceof SenderViewHolder) {

            ((SenderViewHolder) holder)
                    .senderMessage
                    .setText(message.message);

            ((SenderViewHolder) holder)
                    .senderTime
                    .setText(time);

        } else {

            ((ReceiverViewHolder) holder)
                    .receiverMessage
                    .setText(message.message);

            ((ReceiverViewHolder) holder)
                    .receiverTime
                    .setText(time);
        }
    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    // =========================
    // SENDER VIEW HOLDER
    // =========================

    public static class SenderViewHolder
            extends RecyclerView.ViewHolder {

        TextView senderMessage;
        TextView senderTime;

        public SenderViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            senderMessage =
                    itemView.findViewById(
                            R.id.senderMessage
                    );

            senderTime =
                    itemView.findViewById(
                            R.id.senderTime
                    );
        }
    }

    // =========================
    // RECEIVER VIEW HOLDER
    // =========================

    public static class ReceiverViewHolder
            extends RecyclerView.ViewHolder {

        TextView receiverMessage;
        TextView receiverTime;

        public ReceiverViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            receiverMessage =
                    itemView.findViewById(
                            R.id.receiverMessage
                    );

            receiverTime =
                    itemView.findViewById(
                            R.id.receiverTime
                    );
        }
    }
}