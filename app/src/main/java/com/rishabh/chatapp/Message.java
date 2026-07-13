package com.rishabh.chatapp;

public class Message {

    public String messageId;

    public String message;

    public String senderId;

    public String receiverId;

    public long timestamp;

    // Message status

    public boolean delivered;

    public long deliveredTime;

    public boolean seen;

    public long seenTime;

    public String status;

    // Message type

    public String type;

    public String imageUrl;

    // Future features

    public String audioUrl;

    public String replyToMessageId;

    public String reaction;

    public transient boolean isDateChip = false;
    public transient String dateText = "";

    public Message() {
    }

    public Message(
            String message,
            String senderId,
            String receiverId,
            long timestamp
    ) {

        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.status = "sending";

        this.type = "text";
        this.imageUrl = "";
        this.audioUrl = "";
        this.replyToMessageId = "";
        this.reaction = "";

        this.delivered = false;
        this.deliveredTime = 0;

        this.seen = false;
        this.seenTime = 0;
    }
}