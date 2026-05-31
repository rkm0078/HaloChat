package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendProfileActivity
        extends AppCompatActivity {

    TextView fullName;
    TextView username;

    LinearLayout messageBtn;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_friends_profile
        );

        fullName =
                findViewById(R.id.fullName);

        username =
                findViewById(R.id.username);

        messageBtn =
                findViewById(R.id.messageBtn);

        uid =
                getIntent().getStringExtra("uid");

        if (uid == null) {

            finish();

            return;
        }

        loadUser();

        messageBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            FriendProfileActivity.this,
                            ChatActivity.class
                    );

            intent.putExtra("uid", uid);

            intent.putExtra(
                    "username",
                    username.getText().toString()
                            .replace("@", "")
            );

            startActivity(intent);
        });
    }

    private void loadUser() {

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(uid)

                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                User user =
                                        snapshot.getValue(User.class);

                                if (user == null)
                                    return;

                                if (user.getFullName() != null &&
                                        !user.getFullName().isEmpty()) {

                                    fullName.setText(
                                            user.getFullName()
                                    );

                                } else {

                                    fullName.setText("User");
                                }

                                if (user.username != null) {

                                    username.setText(
                                            "@" + user.username
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }
}