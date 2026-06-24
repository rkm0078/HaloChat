package com.rishabh.chatapp.models;

public class Call {

    public String callId;
    public String userId;
    public String userName;
    public String profileImage;

    public long timestamp;

    public boolean isVideo;
    public boolean isIncoming;
    public boolean isMissed;

    public Call() {
    }
}