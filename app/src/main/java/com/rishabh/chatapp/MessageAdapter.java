package com.rishabh.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private final ArrayList<Message> messages;

    private final String receiverUid;

    private static final int ITEM_SENT = 1;
    private static final int ITEM_RECEIVE = 2;

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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return ITEM_RECEIVE;
        }

        String currentUid =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        return message.senderId.equals(currentUid)
                ? ITEM_SENT
                : ITEM_RECEIVE;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == ITEM_SENT) {

            View view = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_sender,
                            parent,
                            false
                    );

            return new SenderViewHolder(view);

        } else {

            View view = LayoutInflater.from(context)
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
                formatter.format(
                        new Date(message.timestamp)
                );

        if (holder instanceof SenderViewHolder) {

            SenderViewHolder senderHolder =
                    (SenderViewHolder) holder;

            senderHolder.senderMessage.setText(
                    message.message
            );

            senderHolder.senderTime.setText(
                    time
            );

            // Message Status

            if (message.seen) {

                senderHolder.seenStatus.setText(
                        "✓✓ Seen"
                );

            } else if (message.delivered) {

                senderHolder.seenStatus.setText(
                        "✓✓ Delivered"
                );

            } else {

                senderHolder.seenStatus.setText(
                        "✓ Sent"
                );
            }

        } else {

            ReceiverViewHolder receiverHolder =
                    (ReceiverViewHolder) holder;

            receiverHolder.receiverMessage.setText(
                    message.message
            );

            receiverHolder.receiverTime.setText(
                    time
            );
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

        final TextView senderMessage;
        final TextView senderTime;
        final TextView seenStatus;

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

            seenStatus =
                    itemView.findViewById(
                            R.id.seenStatus
                    );
        }
    }

    // =========================
    // RECEIVER VIEW HOLDER
    // =========================

    public static class ReceiverViewHolder
            extends RecyclerView.ViewHolder {

        final TextView receiverMessage;
        final TextView receiverTime;

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
    private static final SimpleDateFormat formatter =
            new SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
            );
}