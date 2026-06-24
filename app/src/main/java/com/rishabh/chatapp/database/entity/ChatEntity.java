package com.rishabh.chatapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats")
public class ChatEntity {

    @PrimaryKey
    @NonNull
    public String chatId;

    // OLD FIELDS (keep them)
    public String user1;

    public String user2;

    // NEW FIELDS
    public String friendUid;

    public String friendName;

    public String profileImage;

    public String lastMessage;

    public long lastTimestamp;

    public int unreadCount;

    @Ignore
    public ChatEntity(
            String chatId,
            String user1,
            String user2,
            String friendUid,
            String friendName,
            String profileImage,
            String lastMessage,
            long lastTimestamp,
            int unreadCount) {

        this.chatId = chatId;
        this.user1 = user1;
        this.user2 = user2;
        this.friendUid = friendUid;
        this.friendName = friendName;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
        this.unreadCount = unreadCount;
    }

    public ChatEntity() {
    }
}
