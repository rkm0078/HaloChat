package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    // =========================
    // VIEWS
    // =========================

    ImageView backBtn;

    LinearLayout sendBtn;

    EditText messageBox;

    TextView username;
    TextView statusText;

    RecyclerView chatList;

    // =========================
    // DATA
    // =========================

    ArrayList<Message> messages;

    MessageAdapter adapter;

    FirebaseAuth auth;

    String currentUid;

    String receiverUid;

    String senderRoom;
    String receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        // =========================
        // FIND VIEWS
        // =========================

        backBtn =
                findViewById(R.id.backBtn);

        sendBtn =
                findViewById(R.id.sendBtn);

        messageBox =
                findViewById(R.id.messageBox);

        username =
                findViewById(R.id.username);

        statusText =
                findViewById(R.id.statusText);

        chatList =
                findViewById(R.id.chatList);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getUid();

        if (currentUid == null) {

            finish();

            return;
        }

        // =========================
        // ONLINE STATUS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUid)
                .child("status")
                .setValue("Online");

        // =========================
        // INTENT DATA
        // =========================

        receiverUid =
                getIntent().getStringExtra("uid");

        if (receiverUid == null) {

            finish();

            return;
        }

        String usernameText =
                getIntent().getStringExtra("username");

        if (usernameText != null &&
                !usernameText.isEmpty()) {

            username.setText(usernameText);

        } else {

            username.setText("User");
        }

        // =========================
        // ROOMS
        // =========================

        senderRoom =
                currentUid + receiverUid;

        receiverRoom =
                receiverUid + currentUid;

        // =========================
        // RECYCLER VIEW
        // =========================

        messages = new ArrayList<>();

        adapter =
                new MessageAdapter(
                        this,
                        messages,
                        currentUid
                );

        LinearLayoutManager manager =
                new LinearLayoutManager(this);

        manager.setStackFromEnd(true);

        chatList.setLayoutManager(manager);

        chatList.setAdapter(adapter);

        // =========================
        // LOAD RECEIVER STATUS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(receiverUid)
                .child("status")

                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                String status =
                                        snapshot.getValue(
                                                String.class
                                        );

                                if (status != null &&
                                        !status.isEmpty()) {

                                    statusText.setText(status);

                                } else {

                                    statusText.setText(
                                            "Offline"
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        // =========================
        // LOAD MESSAGES
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(senderRoom)

                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                messages.clear();

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    Message message =
                                            data.getValue(
                                                    Message.class
                                            );

                                    if (message != null) {

                                        messages.add(message);
                                    }
                                }

                                adapter.notifyDataSetChanged();

                                if (messages.size() > 0) {

                                    chatList.scrollToPosition(
                                            messages.size() - 1
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        // =========================
        // SEND MESSAGE
        // =========================

        sendBtn.setOnClickListener(v -> {

            String text =
                    messageBox.getText()
                            .toString()
                            .trim();

            if (text.isEmpty()) {
                return;
            }

            Message message =
                    new Message(
                            text,
                            currentUid,
                            System.currentTimeMillis()
                    );

            FirebaseDatabase.getInstance()
                    .getReference("Chats")
                    .child(senderRoom)
                    .push()
                    .setValue(message);

            FirebaseDatabase.getInstance()
                    .getReference("Chats")
                    .child(receiverRoom)
                    .push()
                    .setValue(message);

            // LAST MESSAGE

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(receiverUid)
                    .child("lastMessage")
                    .setValue(text);

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(receiverUid)
                    .child("lastMessageTime")
                    .setValue(System.currentTimeMillis());

            messageBox.setText("");
        });

        // =========================
        // BACK
        // =========================

        backBtn.setOnClickListener(v -> finish());
    }

    // =========================
    // OFFLINE STATUS
    // =========================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (currentUid != null) {

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("status")
                    .setValue("Offline");
        }
    }
}