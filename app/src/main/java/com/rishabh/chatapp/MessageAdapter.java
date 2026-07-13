package com.rishabh.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private static final int ITEM_SENT = 1;
    private static final int ITEM_RECEIVE = 2;

    private static final int ITEM_DATE = 3;

    public MessageAdapter(
            Context context,
            ArrayList<Message> messages
    ) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);

        if (message.isDateChip) {
            return ITEM_DATE;
        }

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

    public static class DateViewHolder
            extends RecyclerView.ViewHolder {

        final TextView dateText;

        public DateViewHolder(@NonNull View itemView) {

            super(itemView);

            dateText =
                    itemView.findViewById(
                            R.id.dateText
                    );
        }
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == ITEM_DATE) {

            View view = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_date_chip,
                            parent,
                            false
                    );

            return new DateViewHolder(view);
        }

        if (viewType == ITEM_SENT) {

            View view = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_send_message,
                            parent,
                            false
                    );

            return new SenderViewHolder(view);

        } else {

            View view = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_received_message,
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

        if (holder instanceof DateViewHolder) {

            ((DateViewHolder) holder)
                    .dateText
                    .setText(message.dateText);

            return;
        }

        String time =
                formatter.format(
                        new Date(message.timestamp)
                );

        if (holder instanceof SenderViewHolder) {

            SenderViewHolder senderHolder =
                    (SenderViewHolder) holder;

            senderHolder.messageText.setText(
                    message.message
            );

            senderHolder.timeText.setText(
                    time
            );

            if ("sending".equals(message.status)) {

                senderHolder.messageStatusIcon.setImageResource(
                        R.drawable.ic_clock
                );

            } else if ("sent".equals(message.status)) {

                senderHolder.messageStatusIcon.setImageResource(
                        R.drawable.ic_single_tick
                );

            } else if ("delivered".equals(message.status)) {

                senderHolder.messageStatusIcon.setImageResource(
                        R.drawable.ic_double_tick
                );

            } else if ("seen".equals(message.status)) {

                senderHolder.messageStatusIcon.setImageResource(
                        R.drawable.ic_double_tick_seen
                );

            } else {

                senderHolder.messageStatusIcon.setImageResource(
                        R.drawable.ic_clock
                );
            }

            // Message Status


        } else {

            ReceiverViewHolder receiverHolder =
                    (ReceiverViewHolder) holder;

            receiverHolder.messageText.setText(
                    message.message
            );

            receiverHolder.timeText.setText(
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

        final TextView messageText;
        final TextView timeText;
        final ImageView messageStatusIcon;

        public SenderViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            messageText =
                    itemView.findViewById(R.id.messageText);

            timeText =
                    itemView.findViewById(R.id.timeText);

            messageStatusIcon =
                    itemView.findViewById(
                            R.id.messageStatusIcon
                    );
        }
    }

    // =========================
    // RECEIVER VIEW HOLDER
    // =========================

    public static class ReceiverViewHolder
            extends RecyclerView.ViewHolder {

        final TextView messageText;
        final TextView timeText;

        public ReceiverViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            messageText =
                    itemView.findViewById(R.id.messageText);

            timeText =
                    itemView.findViewById(R.id.timeText);
        }
    }

    private static final SimpleDateFormat formatter =
            new SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
            );
}
