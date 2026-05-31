package com.rishabh.chatapp;

public class User {

    // =========================
    // BASIC INFO
    // =========================

    public String uid;

    public String firstName;

    public String lastName;

    public String username;

    public String email;

    // =========================
    // PROFILE
    // =========================

    public String profileImage = "default";

    // =========================
    // ONLINE STATUS
    // =========================

    public String status = "Offline";

    // =========================
    // CHAT INFO
    // =========================

    public String lastMessage = "";

    public long lastMessageTime = 0;

    // =========================
    // EMPTY CONSTRUCTOR
    // =========================

    public User() {

    }

    // =========================
    // MAIN CONSTRUCTOR
    // =========================

    public User(
            String uid,
            String firstName,
            String lastName,
            String username,
            String email,
            String profileImage,
            String status,
            String lastMessage,
            long lastMessageTime
    ) {

        this.uid = uid;

        this.firstName = firstName;

        this.lastName = lastName;

        this.username = username;

        this.email = email;

        this.profileImage =
                profileImage != null
                        ? profileImage
                        : "default";

        this.status =
                status != null
                        ? status
                        : "Offline";

        this.lastMessage =
                lastMessage != null
                        ? lastMessage
                        : "";

        this.lastMessageTime =
                lastMessageTime;
    }

    // =========================
    // FULL NAME
    // =========================

    public String getFullName() {

        String first =
                firstName != null
                        ? firstName
                        : "";

        String last =
                lastName != null
                        ? lastName
                        : "";

        return (first + " " + last).trim();
    }

    // =========================
    // SAFE USERNAME
    // =========================

    public String getSafeUsername() {

        if (username != null &&
                !username.isEmpty()) {

            return username;
        }

        return "User";
    }

    // =========================
    // SAFE PROFILE IMAGE
    // =========================

    public String getSafeProfileImage() {

        if (profileImage != null &&
                !profileImage.isEmpty()) {

            return profileImage;
        }

        return "default";
    }

    // =========================
    // SAFE STATUS
    // =========================

    public String getSafeStatus() {

        if (status != null &&
                !status.isEmpty()) {

            return status;
        }

        return "Offline";
    }
}