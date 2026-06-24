package com.rishabh.chatapp.database.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends")
public class FriendEntity {

    @PrimaryKey
    @NonNull
    public String uid;

    public String friendName;

    public String username;

    public String profileImage;

    public String status;

    public long lastSeen;

    public boolean isOnline;
}