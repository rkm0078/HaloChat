package com.rishabh.chatapp;

public class Message {

    public String message;

    public String senderId;

    public long timestamp;

    public boolean seen = false;
    public long seenTime = 0;

    public Message() {

    }

    public Message(
            String message,
            String senderId,
            long timestamp
    ) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
}