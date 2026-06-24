package com.rishabh.chatapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessageEntity {

    @PrimaryKey
    @NonNull
    public String messageId;

    public String senderId;

    public String receiverId;

    public String message;

    public long timestamp;

    // Message type

    public String type;

    public String imageUrl;

    // Future support

    public String audioUrl;

    public String replyToMessageId;

    public String reaction;

    // Status

    public String status;

    public boolean delivered;

    public long deliveredTime;
}