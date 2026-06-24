package com.rishabh.chatapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calls")
public class CallEntity {

    @PrimaryKey
    @NonNull
    public String callId;

    public String userId;
    public String userName;
    public String profileImage;

    public long timestamp;

    public boolean isVideo;
    public boolean isIncoming;
    public boolean isMissed;

    public CallEntity(@NonNull String callId) {
        this.callId = callId;
    }
}