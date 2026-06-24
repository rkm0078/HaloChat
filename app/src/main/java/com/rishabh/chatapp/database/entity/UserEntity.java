package com.rishabh.chatapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    public String uid;

    public String firstName;

    public String lastName;

    public String username;

    public String email;

    public String profileImage;

    public String status;

    public long lastSeen;

    public String lastMessage;

    public long lastMessageTime;

    public int unreadCount;

    public String bio;

}